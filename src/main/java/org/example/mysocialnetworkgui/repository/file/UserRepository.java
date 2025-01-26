package org.example.mysocialnetworkgui.repository.file;

import org.example.mysocialnetworkgui.domain.User;
import org.example.mysocialnetworkgui.domain.validators.Validator;

public class UserRepository extends AbstractFileRepository<Long, User>{
    public UserRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public User createEntity(String line) {
        String[] splited = line.split(";");
        User u = new User(splited[1], splited[2], splited[3]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String saveEntity(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }

    public Long giveNewId() {
        if (entities.isEmpty())
            return 0L;
        return (long) entities.size();
    }
}
