USE payment_center;

INSERT INTO pay_channel (channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status)
SELECT
    'WX_NATIVE',
    '微信原生扫码',
    'WECHAT',
    'wx_mch_10001',
    JSON_OBJECT(
        'gatewayUrl', 'https://api.mch.weixin.qq.com/v3/pay/transactions/native',
        'mchId', 'wx_mch_10001',
        'appId', 'wx_app_10001',
        'apiV3Key', 'replace-with-real-api-v3-key',
        'serialNo', 'replace-with-cert-serial-no',
        'privateKey', 'replace-with-private-key-pem'
    ),
    0.003500,
    1
WHERE NOT EXISTS (SELECT 1 FROM pay_channel WHERE channel_code = 'WX_NATIVE');

INSERT INTO pay_channel (channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status)
SELECT
    'ALI_QR',
    '支付宝当面付',
    'ALIPAY',
    'ali_mch_10001',
    JSON_OBJECT(
        'gatewayUrl', 'https://openapi.alipay.com/gateway.do',
        'appId', 'ali_app_10001',
        'privateKey', 'replace-with-rsa-private-key',
        'alipayPublicKey', 'replace-with-alipay-public-key',
        'signType', 'RSA2'
    ),
    0.003800,
    1
WHERE NOT EXISTS (SELECT 1 FROM pay_channel WHERE channel_code = 'ALI_QR');

INSERT INTO pay_channel (channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status)
SELECT
    'ALLINPAY_POS',
    '通联POS支付',
    'ALLINPAY',
    'allinpay_mch_10001',
    JSON_OBJECT(
        'gatewayUrl', 'https://vsp.allinpay.com/apiweb/unitorder/pay',
        'orgId', 'replace-with-orgid',
        'cusid', 'replace-with-cusid',
        'appId', 'replace-with-appid',
        'key', 'replace-with-key'
    ),
    0.004000,
    1
WHERE NOT EXISTS (SELECT 1 FROM pay_channel WHERE channel_code = 'ALLINPAY_POS');
