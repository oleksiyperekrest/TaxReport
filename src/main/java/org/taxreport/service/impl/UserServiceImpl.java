package org.taxreport.service.impl;

import org.taxreport.dao.ClientDao;
import org.taxreport.dao.DaoPool;
import org.taxreport.dao.PersonnelDao;
import org.taxreport.dao.UserDao;
import org.taxreport.entity.Client;
import org.taxreport.entity.Personnel;
import org.taxreport.entity.User;
import org.taxreport.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private DaoPool daoPool;

    public UserServiceImpl(DaoPool daoPool) {
        this.daoPool = daoPool;
    }

    @Override
    public void create(User user) {
        if (user instanceof Personnel) {
            daoPool.getPersonnelDao().create((Personnel) user);
        } else if (user instanceof Client) {
            daoPool.getClientDao().create((Client) user);
        }
    }

    @Override
    public List<Client> getAllClients() {
        return daoPool.getClientDao().getAll();
    }

    @Override
    public List<Personnel> getAllPersonnel() {
        return daoPool.getPersonnelDao().getAll();
    }

    @Override
    public Optional<Client> getClientById(Long id) {
        return daoPool.getClientDao().getById(id);
    }

    @Override
    public Optional<Personnel> getPersonnelById(Long id) {
        return daoPool.getPersonnelDao().getById(id);
    }

    @Override
    public Optional<? extends User> getByEmail(String email) {
        Optional<Personnel> personnel = daoPool.getPersonnelDao().getByEmail(email);
        if (!personnel.isPresent()) {
            return daoPool.getClientDao().getByEmail(email);
        }
        return personnel;
    }


    @Override
    public boolean login(String email, String password) {
        if (email.contains("taxreport.org")) { //TODO check for email pattern
            return loginWithDao(email, password, daoPool.getPersonnelDao());
        } else {
            return loginWithDao(email, password, daoPool.getClientDao());
        }
    }

    private boolean loginWithDao(String email, String password, UserDao userDao) {
        if (userDao instanceof ClientDao) {
            ClientDao dao = (ClientDao) userDao;
            Client client = dao.getByEmail(email).get();
            String passwordDB = client.getPassword();
            return password.equals(passwordDB);
        }

        if (userDao instanceof PersonnelDao) {
            PersonnelDao dao = (PersonnelDao) userDao;
            Personnel personnel = dao.getByEmail(email).get();
            String passwordDB = personnel.getPassword();
            return password.equals(passwordDB);
        }
        return false;
    }

}
