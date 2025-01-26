package org.example.mysocialnetworkgui.repository.file;
import org.example.mysocialnetworkgui.domain.Friendship;
import org.example.mysocialnetworkgui.domain.validators.Validator;

public class FriendshipRepository extends AbstractFileRepository<Long, Friendship>{
    public FriendshipRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Friendship createEntity(String line) {
        String[] splitted = line.split(";");
        Friendship f = new Friendship(Long.parseLong(splitted[1]), Long.parseLong(splitted[2]));
        f.setId(Long.parseLong(splitted[0]));
        return f;
    }

    @Override
    public String saveEntity(Friendship entity) {
        return entity.getId() + ";" + entity.getFirstUserId() + ";" + entity.getSecondUserId();
    }

    @Override
    public Long giveNewId() {
        if (entities.isEmpty())
            return 0L;
        return (long) entities.size();
    }
}
