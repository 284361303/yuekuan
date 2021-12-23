package m.fasion.ai.util.customize;

import androidx.fragment.app.FragmentManager;

import java.util.Objects;

/**
 * 加载框工具类
 */
public class ProgressDialog {

    private static LoadingDialog mDialog;

    public static void showProgress(FragmentManager context) {
        showProgress(context, null);
    }

    /**
     * 显示加载框
     */
    public static void showProgress(FragmentManager context, LoadingDialog dialog) {
        dismissProgress();
        if (dialog == null) {
            dialog = new LoadingDialog();
        }
        mDialog = dialog;
        mDialog.show(context, "loading");
    }

    /**
     * 加载框消失
     */
    public static void dismissProgress() {
        try {
            if (mDialog != null && Objects.requireNonNull(mDialog.getDialog()).isShowing()) {
                mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseDialog();
        }
    }

    private static void releaseDialog() {
        mDialog = null;
    }
}