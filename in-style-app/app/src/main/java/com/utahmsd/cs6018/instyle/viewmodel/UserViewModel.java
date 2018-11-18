package com.utahmsd.cs6018.instyle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.db.repo.UserRepository;


public class UserViewModel extends AndroidViewModel {

    private static final String LOG_TAG = UserViewModel.class.getSimpleName();

    private final MediatorLiveData<User> m_observableUser;

    private final UserRepository m_userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        InStyleDatabase database = InStyleDatabase.getDatabaseInstance(application);
        m_userRepository = UserRepository.getInstance(database);

        m_observableUser = new MediatorLiveData<>();

        configureMediatorLiveData();
    }

    private void configureMediatorLiveData() {
        m_observableUser.setValue(null);

        LiveData<User> userLiveData = m_userRepository.getUser();
        m_observableUser.addSource(userLiveData, user -> {
            Log.d(LOG_TAG, "m_userRepository.getUser() listener onChanged");
            if (user == null) {
                Log.d(LOG_TAG, "BUT ALAS IT IS NULL :(");
                return;
            }
            m_observableUser.setValue(user);

        });
    }

    public boolean authenticateUser(User user) {
        if (user == null) {
            return false;
        }
        LiveData<User> result = m_userRepository.find(user.getEmail());
        if (result.getValue() == null) {
            return false;
        }
        return result.getValue().getEmail().equals(user.getEmail()) &&
                result.getValue().getPassword().equals(user.getPassword());
    }

    public void createUser(User user) {
        m_userRepository.insert(user);
        m_observableUser.setValue(user);
    }

    public void updateUser(User user) {
        m_userRepository.update(user);
    }

    public void deleteUser(User user) {
        m_userRepository.delete(user);
    }

    public LiveData<User> findUser(String email) {
        return m_userRepository.find(email);
    }

    public LiveData<User> getUser() {
        return m_observableUser;
    }

    public void resetUser() {
        m_observableUser.setValue(null);

    }

}
