//在使用的地方直接复制
if (checkSelfPermissionCompat(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
    //允许了权限 
    //startCamera()
} else {
    //去请求权限
}