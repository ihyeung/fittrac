package com.utahmsd.cs6018.instyle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.db.repo.UserRepository;

import java.util.List;

public class UserListViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<User>> m_observableUserList;

    private final UserRepository m_userRepository;

    public UserListViewModel(@NonNull Application application) {
        super(application);
        InStyleDatabase database = InStyleDatabase.getDatabaseInstance(application);
        m_userRepository = UserRepository.getInstance(database);

        m_observableUserList = new MediatorLiveData<>();

        configureMediatorLiveData();
    }

    private void configureMediatorLiveData() {
        m_observableUserList.setValue(null);

        LiveData<List<User>> users = m_userRepository.getUsers();

        m_observableUserList.addSource(users, m_observableUserList::setValue);
    }

    public void resetUserTable(int numUsers) {
        m_userRepository.asyncResetUserDatabase(numUsers);
    }

    public LiveData<List<User>> getUserList() {
        return m_observableUserList;
    }
}
