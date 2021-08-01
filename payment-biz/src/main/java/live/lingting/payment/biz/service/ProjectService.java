package live.lingting.payment.biz.service;

import com.hccake.ballcat.common.model.domain.PageResult;
import com.hccake.extend.mybatis.plus.service.ExtendService;
import java.util.List;
import live.lingting.payment.Page;
import live.lingting.payment.entity.Project;
import live.lingting.payment.enums.ProjectMode;
import live.lingting.payment.enums.ProjectScope;

/**
 * @author lingting 2021/6/4 13:36
 */
public interface ProjectService extends ExtendService<Project> {

	/**
	 * 搜索
	 * @param page 分页
	 * @param project 项目
	 * @return com.hccake.ballcat.common.model.domain.PageResult<live.lingting.entity.Project>
	 * @author lingting 2021-06-04 16:32
	 */
	PageResult<Project> list(Page<Project> page, Project project);

	/**
	 * 新增项目
	 * @param project 项目
	 * @author lingting 2021-06-04 17:15
	 */
	void create(Project project);

	/**
	 * 重置API
	 * @param id 项目 Id
	 * @author lingting 2021-06-04 17:19
	 */
	void resetApi(Integer id);

	/**
	 * 更新禁用信息
	 * @param id id
	 * @param disabled 是否禁用
	 * @author lingting 2021-06-04 17:26
	 */
	void disabled(Integer id, Boolean disabled);

	/**
	 * 更新项目模式
	 * @param ids 项目
	 * @param mode 新模式
	 * @author lingting 2021-07-07 10:15
	 */
	void mode(List<Integer> ids, ProjectMode mode);

	/**
	 * 根据apiKey 获取项目信息
	 * @param key key
	 * @return live.lingting.entity.Project
	 * @author lingting 2021-06-07 16:39
	 */
	Project getByApiKey(String key);

	/**
	 * 更新项目权限
	 * @param ids id
	 * @param scopes 新权限
	 * @author lingting 2021-07-16 16:25
	 */
	void scope(List<Integer> ids, List<ProjectScope> scopes);

}