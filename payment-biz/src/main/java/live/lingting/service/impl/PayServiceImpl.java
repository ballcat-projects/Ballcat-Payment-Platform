package live.lingting.service.impl;

import com.hccake.ballcat.common.core.exception.BusinessException;
import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.impl.ExtendServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import live.lingting.Page;
import live.lingting.Redis;
import live.lingting.constant.PayConstants;
import live.lingting.entity.Pay;
import live.lingting.entity.Project;
import live.lingting.entity.VirtualAddress;
import live.lingting.enums.ResponseCode;
import live.lingting.mapper.PayMapper;
import live.lingting.sdk.enums.PayStatus;
import live.lingting.sdk.model.MixVirtualPayModel;
import live.lingting.service.PayService;
import live.lingting.service.VirtualAddressService;

/**
 * @author lingting 2021/6/4 13:40
 */
@Service
@RequiredArgsConstructor
public class PayServiceImpl extends ExtendServiceImpl<PayMapper, Pay> implements PayService {

	private final Redis redis;

	private final VirtualAddressService virtualAddressService;

	@Override
	public PageResult<Pay> list(Page<Pay> page, Pay pay) {
		return baseMapper.list(page, pay);
	}

	@Override
	public List<Pay> list(Pay pay) {
		return baseMapper.selectList(baseMapper.getWrapper(pay));
	}

	@Override
	public Pay getByNo(String tradeNo, String projectTradeNo) {
		Pay pay = new Pay();

		if (StringUtils.hasText(tradeNo)) {
			pay.setTradeNo(tradeNo);
		}
		else {
			pay.setProjectTradeNo(projectTradeNo);
		}

		return baseMapper.selectOne(baseMapper.getWrapper(pay));
	}

	@Override
	public long count(Pay pay) {
		return baseMapper.selectCount(baseMapper.getWrapper(pay));
	}

	@Override
	public Pay virtualCreate(MixVirtualPayModel model, Project project) {
		VirtualAddress va = virtualAddressService.lock(model);

		if (va == null) {
			throw new BusinessException(ResponseCode.NO_ADDRESS_AVAILABLE);
		}
		Pay pay = new Pay().setAddress(va.getAddress()).setChain(model.getChain())
				.setCurrency(model.getContract().getCurrency()).setNotifyUrl(model.getNotifyUrl())
				.setProjectTradeNo(model.getProjectTradeNo());
		create(project, pay);
		return pay;
	}

	private void create(Project project, Pay pay) {
		String key = PayConstants.getProjectTradeNoLock(project.getId(), pay.getProjectTradeNo());

		if (!redis.setIfAbsent(key, "", TimeUnit.DAYS.toSeconds(1))) {
			throw new BusinessException(ResponseCode.PROJECT_NO_REPEAT);
		}

		// 上锁成功, 从数据库查询
		if (count(new Pay().setProjectTradeNo(pay.getProjectTradeNo())) > 0) {
			throw new BusinessException(ResponseCode.PROJECT_NO_REPEAT);
		}

		pay.setProjectId(project.getId()).setStatus(PayStatus.WAIT);
		try {
			if (!save(pay)) {
				throw new BusinessException(ResponseCode.PAY_SAVE_FAIL);
			}
		}
		catch (Exception e) {
			redis.delete(key);
			throw e;
		}
	}

	@Override
	public boolean virtualSubmit(String tradeNo, String hash) {
		return baseMapper.virtualSubmit(tradeNo, hash);
	}

	@Override
	public boolean fail(String tradeNo, String desc, LocalDateTime retryEndTime) {
		return baseMapper.fail(tradeNo, desc, retryEndTime);
	}

	@Override
	public boolean success(Pay pay) {
		return baseMapper.success(pay.getTradeNo());
	}

}
