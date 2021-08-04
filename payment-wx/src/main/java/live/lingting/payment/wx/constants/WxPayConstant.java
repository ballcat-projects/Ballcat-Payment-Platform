package live.lingting.payment.wx.constants;

import java.math.BigDecimal;

/**
 * @author lingting 2021/1/26 16:15
 */
public class WxPayConstant {

	/**
	 * 一百
	 */
	public static final BigDecimal HUNDRED = new BigDecimal("100");

	/**
	 * 签名字段名
	 */
	public static final String FIELD_SIGN = "sign";

	/**
	 * 签名类型字段名
	 */
	public static final String FIELD_SIGN_TYPE = "sign_type";

	/**
	 * 回调成功返回值
	 */
	public static String CALLBACK_SUCCESS = "<xml>\n" + "  <return_code><![CDATA[SUCCESS]]></return_code>\n"
			+ "  <return_msg><![CDATA[OK]]></return_msg>\n" + "</xml>";

	/**
	 * 回调验签失败返回值
	 */
	public static String CALLBACK_SIGN_ERROR = "<xml>\n" + "  <return_code><![CDATA[FAIL]]></return_code>\n"
			+ "  <return_msg><![CDATA[签名异常]]></return_msg>\n" + "</xml>";

}
