package rw.twaza.umushoferi.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import rw.twaza.umushoferi.Common.Common;
import rw.twaza.umushoferi.mode.Token;




public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Common.currentToken=refreshedToken;
        updateTokenToserver(refreshedToken);
    }

    private void updateTokenToserver(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens =db.getReference(Common.tokeni_tbl);
        Token token= new Token(refreshedToken);
        if (FirebaseAuth.getInstance().getCurrentUser() !=null)  // if is aready log in   must up date Token
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
    }
}
