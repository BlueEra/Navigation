package cn.zerokirby.api.data

/**
 * 常量类
 */
object Constants {
    const val LOCAL_DATABASE = "ZerokirbyAPI.db" //本地数据库
    const val ROOT_URL = "https://zerokirby.cn:8443/progress_note_server/" //天天服务器根目录
    const val LOGIN_URL = ROOT_URL + "LoginServlet" //登录服务程序
    const val REGISTER_URL = ROOT_URL + "RegisterServlet" //注册服务程序
    const val DOWNLOAD_AVATAR_URL = ROOT_URL + "DownloadAvatarServlet" //下载头像服务程序
    const val UPLOAD_AVATAR_URL = ROOT_URL + "UploadAvatarServlet" //上传头像服务程序
    const val CHOOSE_PHOTO = 1 //选择图片
    const val PHOTO_REQUEST_CUT = 2 //请求裁剪图片
}