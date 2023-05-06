package com.freelycar.demo.giftrain;

public class BoxInfo {
    //红包ID（拿这个去问服务器是否中奖）
    private int awardId;
    //红包校验
    private String voucher;

    public int getAwardId() {
        return awardId;
    }

    public BoxInfo setAwardId(int awardId) {
        this.awardId = awardId;
        return this;
    }

    public String getVoucher() {
        return voucher;
    }

    public BoxInfo setVoucher(String voucher) {
        this.voucher = voucher;
        return this;
    }
}
