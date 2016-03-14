# ShortcutLib
因为公司游戏产品需要，基于 [xuyisheng/ShortcutHelper](!https://github.com/xuyisheng/ShortcutHelper) 二次开发，本项目使用更加简单

## 项目意义

快速使用shortcut，避免各种ROM适配导致的各种问题。


1 使用本地资源icon创建桌面快捷方式

2 使用网络图片作为icon创建桌面快捷方式

后续会增加移除快捷方式，尝试增加Badge等功能，待开发

## 项目可用功能API


## 使用示例

 java

     new Sc.Builder(this, this).
                setName("资源创建快捷方式").
                setAllowRepeat(false).
                setIcon(R.mipmap.ic_launcher).
                setCallBack(new ScCreateResultCallback() {
                    @Override
                    public void createSuccessed(String createdOrUpdate, Object tag) {
                        Toast.makeText(MainActivity.this, createdOrUpdate, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void createError(String errorMsg, Object tag) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

                    }
                }).build().createSc();

        new Sc.Builder(MainActivity.this, MainActivity.this).
                setAllowRepeat(false).
                setName("网络图片快捷方式").
                setIcon("http://img1.qidian.com/upload/gamesy/2016/03/03/20160303165643tqfnt6pvx0.jpg").
                setCallBack(new ScCreateResultCallback() {
                    @Override
                    public void createSuccessed(String createdOrUpdate, Object tag) {
                        Toast.makeText(MainActivity.this, createdOrUpdate, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void createError(String errorMsg, Object tag) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }).build().createSc();



