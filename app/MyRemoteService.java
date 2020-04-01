//远程Service类
public class MyRemoteService extends Service {
    private class MyBinder extends IMyAidlInterface.Stub{
        //重写Toas方法
        @Override
        public void showToast() throws RemoteException {
            //调用service内部方法
            showToast();
        }
    }
    //内表show Toast方法
    private void showToast(){
        Toast.makeText(this,"romote service:show toast",Toast.LENGTH_SHORT);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
}
