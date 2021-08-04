package live.lingting.payment.wx.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import live.lingting.payment.http.utils.JacksonUtils;
import live.lingting.payment.wx.enums.ResponseCode;
import live.lingting.payment.wx.enums.TradeState;
import live.lingting.payment.wx.enums.TradeType;

/**
 * @author lingting 2021/2/25 15:19
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class WxPayOrderQueryResponse {

	@JsonProperty("transaction_id")
	private String transactionId;

	@JsonProperty("nonce_str")
	private String nonceStr;

	@JsonProperty("trade_state")
	private TradeState tradeState;

	@JsonProperty("bank_type")
	private String bankType;

	@JsonProperty("openid")
	private String openid;

	@JsonProperty("sign")
	private String sign;

	@JsonProperty("return_msg")
	private String returnMsg;

	@JsonProperty("fee_type")
	private String feeType;

	@JsonProperty("mch_id")
	private String mchId;

	@JsonProperty("cash_fee")
	private BigInteger cashFee;

	@JsonProperty("out_trade_no")
	private String outTradeNo;

	@JsonProperty("cash_fee_type")
	private String cashFeeType;

	@JsonProperty("appid")
	private String appid;

	@JsonProperty("total_fee")
	private BigInteger totalFee;

	@JsonProperty("trade_state_desc")
	private String tradeStateDesc;

	@JsonProperty("trade_type")
	private TradeType tradeType;

	@JsonProperty("result_code")
	private ResponseCode resultCode;

	@JsonProperty("attach")
	private String attach;

	@JsonProperty("time_end")
	private String timeEnd;

	@JsonProperty("is_subscribe")
	private String isSubscribe;

	@JsonProperty("return_code")
	private ResponseCode returnCode;

	/**
	 * 返回的原始数据
	 */
	private Map<String, String> raw;

	public static WxPayOrderQueryResponse of(Map<String, String> res) {
		return JacksonUtils.toObj(JacksonUtils.toJson(res), WxPayOrderQueryResponse.class).setRaw(res);
	}

	/**
	 * 交易是否成功 . 返回false 表示交易失败
	 * @return boolean
	 * @author lingting 2021-02-25 15:35
	 */
	public boolean isSuccess() {
		// 交易成功
		return returnCode == ResponseCode.SUCCESS && resultCode == ResponseCode.SUCCESS
				&& tradeState == TradeState.SUCCESS;
	}

}
