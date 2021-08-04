package live.lingting.payment.wx.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import live.lingting.payment.http.utils.JacksonUtils;
import live.lingting.payment.wx.WxPay;
import live.lingting.payment.wx.enums.ResponseCode;
import live.lingting.payment.wx.enums.TradeType;
import java.math.BigInteger;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author lingting 2021/2/25 15:43
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WxPayCallback {

	private String transactionId;

	private String nonceStr;

	private String bankType;

	private String openid;

	private String sign;

	private String feeType;

	private String mchId;

	private BigInteger cashFee;

	private String outTradeNo;

	private String appid;

	private BigInteger totalFee;

	private TradeType tradeType;

	private ResponseCode resultCode;

	private String timeEnd;

	private String isSubscribe;

	private ResponseCode returnCode;

	public static WxPayCallback of(Map<String, String> res) {
		return JacksonUtils.toObj(JacksonUtils.toJson(res), WxPayCallback.class).setRaw(res);
	}

	/**
	 * 返回的原始数据
	 */
	private Map<String, String> raw;

	/**
	 * 验签
	 * @param wxPay 微信支付信息
	 * @return boolean
	 * @author lingting 2021-02-25 16:04
	 */
	public boolean checkSign(WxPay wxPay) {
		return wxPay.checkSign(this);
	}

}
