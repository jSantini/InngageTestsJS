//package br.com.inngage.sdk;
//
///**
// * Maintained by Mohamed Ali Nakouri on 11/05/21.22/10/17.
// */
//
//import android.content.pm.PackageManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import java.util.ArrayList;
//
//public class InngagePermissionUtil {
//
//    static private AppCompatActivity mAppCompatActivity;
//
//    public static PermissionObject with(AppCompatActivity activity) {
//        mAppCompatActivity = activity;
//        return new PermissionObject();
//    }
//
//    public static class PermissionObject {
//
//        public PermissionRequestObject request(String permissionName) {
//            return new PermissionRequestObject(new String[]{permissionName});
//        }
//
//        public PermissionRequestObject request(String... permissionNames) {
//            return new PermissionRequestObject(permissionNames);
//        }
//    }
//
//    static public class PermissionRequestObject {
//
//        private static final String TAG = PermissionObject.class.getSimpleName();
//
//        private ArrayList<SinglePermission> mPermissionsWeDontHave;
//        private int mRequestCode;
//        private GrantFunc mGrantFunc;
//        private GrantFunc mDenyFunc;
//        private GrantPermission mResultFunc;
//        private GrantFunc3 mRationalFunc;
//        private String[] mPermissionNames;
//
//        public PermissionRequestObject(String[] permissionNames) {
//            mPermissionNames = permissionNames;
//        }
//
//        /**
//         * Execute the permission request with the given Request Code
//         *
//         * @param reqCode
//         *         a unique request code in your activity
//         */
//        public PermissionRequestObject ask(int reqCode) {
//            mRequestCode = reqCode;
//            int length = mPermissionNames.length;
//            mPermissionsWeDontHave = new ArrayList<>(length);
//            for (String mPermissionName : mPermissionNames) {
//                mPermissionsWeDontHave.add(new SinglePermission(mPermissionName));
//            }
//
//            if (needToAsk()) {
//                Log.i(TAG, "Asking for permission");
//                ActivityCompat.requestPermissions(mAppCompatActivity, mPermissionNames, reqCode);
//            } else {
//                Log.i(TAG, "No need to ask for permission");
//                if (mGrantFunc != null) mGrantFunc.call();
//            }
//            return this;
//        }
//
//        private boolean needToAsk() {
//            ArrayList<SinglePermission> neededPermissions = new ArrayList<>(mPermissionsWeDontHave);
//            for (int i = 0; i < mPermissionsWeDontHave.size(); i++) {
//                SinglePermission perm = mPermissionsWeDontHave.get(i);
//                int checkRes = ContextCompat.checkSelfPermission(mAppCompatActivity, perm.getPermissionName());
//                if (checkRes == PackageManager.PERMISSION_GRANTED) {
//                    neededPermissions.remove(perm);
//                } else {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(mAppCompatActivity, perm.getPermissionName())) {
//                        perm.setRationalNeeded(true);
//                    }
//                }
//            }
//            mPermissionsWeDontHave = neededPermissions;
//            mPermissionNames = new String[mPermissionsWeDontHave.size()];
//            for (int i = 0; i < mPermissionsWeDontHave.size(); i++) {
//                mPermissionNames[i] = mPermissionsWeDontHave.get(i).getPermissionName();
//            }
//            return mPermissionsWeDontHave.size() != 0;
//        }
//
//        /**
//         * Called for the first denied permission if there is need to show the rational
//         */
//        public PermissionRequestObject onRational(GrantFunc3 rationalFunc) {
//            mRationalFunc = rationalFunc;
//            return this;
//        }
//
//        /**
//         * Called if all the permissions were granted
//         */
//        public PermissionRequestObject onAllGranted(GrantFunc grantFunc) {
//            mGrantFunc = grantFunc;
//            return this;
//        }
//
//        /**
//         * Called if there is at least one denied permission
//         */
//        public PermissionRequestObject onAnyDenied(GrantFunc denyFunc) {
//            mDenyFunc = denyFunc;
//            return this;
//        }
//
//        /**
//         * Called with the original operands from {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])
//         * onRequestPermissionsResult} for any result
//         */
//        public PermissionRequestObject onResult(GrantPermission resultFunc) {
//            mResultFunc = resultFunc;
//            return this;
//        }
//
//        /**
//         * This Method should be called from {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])
//         * onRequestPermissionsResult} with all the same incoming operands
//         * <pre>
//         * {@code
//         *
//         * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//         *      if (mStoragePermissionRequest != null)
//         *          mStoragePermissionRequest.onRequestPermissionsResult(requestCode, permissions,grantResults);
//         * }
//         * }
//         * </pre>
//         */
//        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//            Log.i(TAG, String.format("ReqCode: %d, ResCode: %d, PermissionName: %s", requestCode, grantResults[0], permissions[0]));
//
//            if (mRequestCode == requestCode) {
//                if (mResultFunc != null) {
//                    Log.i(TAG, "Calling Results Func");
//                    mResultFunc.call(requestCode, permissions, grantResults);
//                    return;
//                }
//
//                for (int i = 0; i < permissions.length; i++) {
//                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                        if (mPermissionsWeDontHave.get(i).isRationalNeeded()) {
//                            if (mRationalFunc != null) {
//                                Log.i(TAG, "Calling Rational Func");
//                                mRationalFunc.call(mPermissionsWeDontHave.get(i).getPermissionName());
//                            }
//                        }
//                        if (mDenyFunc != null) {
//                            Log.i(TAG, "Calling Deny Func");
//                            mDenyFunc.call();
//                        } else Log.e(TAG, "NUll DENY FUNCTIONS");
//
//                        // terminate if there is at least one deny
//                        return;
//                    }
//                }
//
//                // there has not been any deny
//                if (mGrantFunc != null) {
//                    Log.i(TAG, "Calling Grant Func");
//                    mGrantFunc.call();
//                } else Log.e(TAG, "NUll GRANT FUNCTIONS");
//            }
//        }
//    }
//}
//
//abstract class GrantFunc {
//    protected abstract void call();
//}
//
//abstract class GrantFunc3 {
//    protected abstract void call(String permissionName);
//}
//
//class SinglePermission {
//
//    private String mPermissionName;
//    private boolean mRationalNeeded = false;
//    private String mReason;
//
//    public SinglePermission(String permissionName) {
//        mPermissionName = permissionName;
//    }
//
//    public SinglePermission(String permissionName, String reason) {
//        mPermissionName = permissionName;
//        mReason = reason;
//    }
//
//    public boolean isRationalNeeded() {
//        return mRationalNeeded;
//    }
//
//    public void setRationalNeeded(boolean rationalNeeded) {
//        mRationalNeeded = rationalNeeded;
//    }
//
//    public String getReason() {
//        return mReason == null ? "" : mReason;
//    }
//
//    public void setReason(String reason) {
//        mReason = reason;
//    }
//
//    public String getPermissionName() {
//        return mPermissionName;
//    }
//
//    public void setPermissionName(String permissionName) {
//        mPermissionName = permissionName;
//    }
//
//}