package live.lingting.sdk.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.sdk.constant.SdkConstants;
import live.lingting.sdk.model.MixRealPayModel;
import live.lingting.sdk.response.MixRealPayResponse;

/**
 * 真实货币 - 下单
 *
 * @author lingting 2021/6/7 17:21
 */
@Getter
@Setter
@Accessors(chain = true)
public class MixRealPayRequest extends AbstractMixRequest<MixRealPayModel, MixRealPayResponse> {

	@Override
	public String getPath() {
		return SdkConstants.MIX_REAL_PAU_PATH;
	}

}
