package busbooking.model;

/**
 * วิธีชำระเงิน (ระบบจำลอง ไม่ได้เชื่อมธนาคารจริง)
 * เก็บในคอลัมน์ method ของ payments.csv เป็นรหัสภาษาอังกฤษ
 */
public enum PaymentMethod {

    PROMPTPAY("พร้อมเพย์"),
    CARD("บัตรเครดิต/เดบิต");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentMethod fromCode(String code) {
        return valueOf(code.trim().toUpperCase());
    }
}
