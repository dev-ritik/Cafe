package io.github.dev_ritik.cafe;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //find all objects in the Client.class
    public RealmResults<Client> getClients() {

        return realm.where(Client.class).findAll();
    }

    //query a single item with the given id
    public Client getClient(String id) {

        return realm.where(Client.class).equalTo("id", id).findFirst();
    }

    //query example
    public RealmResults<Client> queryedClients() {

        return realm.where(Client.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }
}
