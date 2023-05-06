/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.freelycar.plugin.service;
public interface IAidlInterface extends android.os.IInterface
{
  /** Default implementation for IAidlInterface. */
  public static class Default implements IAidlInterface
  {
    @Override public void addPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<com.freelycar.plugin.service.Person> getPersonList() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void setInPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
    {
    }
    @Override public void setOutPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
    {
    }
    @Override public void setInOutPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IAidlInterface
  {
    private static final String DESCRIPTOR = "com.freelycar.plugin.service.IAidlInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.freelycar.plugin.service.IAidlInterface interface,
     * generating a proxy if needed.
     */
    public static IAidlInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IAidlInterface))) {
        return ((IAidlInterface)iin);
      }
      return new Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_addPerson:
        {
          data.enforceInterface(descriptor);
          com.freelycar.plugin.service.Person _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.freelycar.plugin.service.Person.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.addPerson(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getPersonList:
        {
          data.enforceInterface(descriptor);
          java.util.List<com.freelycar.plugin.service.Person> _result = this.getPersonList();
          reply.writeNoException();
          reply.writeTypedList(_result);
          return true;
        }
        case TRANSACTION_setInPerson:
        {
          data.enforceInterface(descriptor);
          com.freelycar.plugin.service.Person _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.freelycar.plugin.service.Person.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.setInPerson(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setOutPerson:
        {
          data.enforceInterface(descriptor);
          com.freelycar.plugin.service.Person _arg0;
          _arg0 = new com.freelycar.plugin.service.Person();
          this.setOutPerson(_arg0);
          reply.writeNoException();
          if ((_arg0!=null)) {
            reply.writeInt(1);
            _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_setInOutPerson:
        {
          data.enforceInterface(descriptor);
          com.freelycar.plugin.service.Person _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.freelycar.plugin.service.Person.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.setInOutPerson(_arg0);
          reply.writeNoException();
          if ((_arg0!=null)) {
            reply.writeInt(1);
            _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IAidlInterface
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void addPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((person!=null)) {
            _data.writeInt(1);
            person.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_addPerson, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().addPerson(person);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<com.freelycar.plugin.service.Person> getPersonList() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<com.freelycar.plugin.service.Person> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPersonList, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getPersonList();
          }
          _reply.readException();
          _result = _reply.createTypedArrayList(com.freelycar.plugin.service.Person.CREATOR);
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setInPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((person!=null)) {
            _data.writeInt(1);
            person.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_setInPerson, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setInPerson(person);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setOutPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setOutPerson, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setOutPerson(person);
            return;
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            person.readFromParcel(_reply);
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setInOutPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((person!=null)) {
            _data.writeInt(1);
            person.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_setInOutPerson, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setInOutPerson(person);
            return;
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            person.readFromParcel(_reply);
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static IAidlInterface sDefaultImpl;
    }
    static final int TRANSACTION_addPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getPersonList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setInPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_setOutPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_setInOutPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    public static boolean setDefaultImpl(IAidlInterface impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static IAidlInterface getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public void addPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException;
  public java.util.List<com.freelycar.plugin.service.Person> getPersonList() throws android.os.RemoteException;
  public void setInPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException;
  public void setOutPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException;
  public void setInOutPerson(com.freelycar.plugin.service.Person person) throws android.os.RemoteException;
}
