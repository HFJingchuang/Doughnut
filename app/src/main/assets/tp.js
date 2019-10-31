deleteWallet('testaddr11111');
  function createWallet(password, name, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.createWallet(password, name, callBack);
      }
  }

  function  deleteWallet(address){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.deleteWallet( address);
      }
  }

  function  exportWalletWithQR(address, widthAndHeight, color){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.exportWalletWithQR(address, widthAndHeight, color);
      }
  }

  function  importWalletWithKey(password, privateKey, name, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.importWalletWithKey(password, privateKey, name, callBack);
      }
  }

  function  importKeysStore(keyStore, password, name, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.importKeysStore(keyStore, password, name, callBack);
      }
  }

  function  getPrivateKey(password, keyStore, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getPrivateKey(password, keyStore, callBack);
      }
  }

  function transfer(privateKey, from, to, token, issuer, value, fee, memo, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.transfer(privateKey, from, to, token, issuer, value, fee, memo, callBack);
      }
  }

  function getTransferHistory(address, limit, marker, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getTransferHistory(address, limit, marker, callBack);
      }
  }

  function getBalance(address, isDisposable, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getBalance(address, isDisposable, callBack);
      }
  }

  function getSWTBalance(address, callBack){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getSWTBalance(address, callBack);
      }
  }

  function getTokenPrice(base, jCallback){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getTokenPrice(base, jCallback);
      }
  }

  function getAllTokenPrice(jCallback){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getAllTokenPrice(jCallback);
      }
  }

  function getAllTokens(){
      if(window.android!=null&&typeof(window.android)!="undefined"){
         window.android.getAllTokens();
      }
  }