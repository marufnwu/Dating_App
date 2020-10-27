package site.zawag.work.Azure;

public interface OnPornListener {
    void onDetectionFailed(String error);
    void OnDetectionSuccess(float score, String type);
}
