package ru.khripunov.socialnetworktt.service.FriendService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Friend;
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.repository.FriendRepository;
import ru.khripunov.socialnetworktt.service.PeopleService.PeopleServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final PeopleServiceImpl peopleServiceImpl;

    @Override
    public void sendInvite(String recipient, Jwt principal) {
        Friend friend = new Friend();
        Person person = (Person) peopleServiceImpl.loadUserByUsername(principal.getSubject());
        Person rec = (Person) peopleServiceImpl.loadUserByUsername(recipient);
        friend.setPersonId(person.getId());
        friend.setFriendId(rec.getId());

        if(friendRepository.findPair(person.getId(), rec.getId()) != 0)
            return;

        friendRepository.save(friend);
    }


    @Override
    public List<String> listOfFriends(Jwt principal) {

        Person person = (Person) peopleServiceImpl.loadUserByUsername(principal.getSubject());

        return friendsList(person.getUsername());

    }

    @Override
    public List<String> listOfFriends(String username) {
        Person person = (Person) peopleServiceImpl.loadUserByUsername(username);
        if(person.getHideFriendsList())
            return new ArrayList<>();
        return friendsList(username);
    }

    @Override
    public boolean checkRelationships(Long personId, Long friendId) {
        if(friendRepository.findPair(personId, friendId) != 0 && friendRepository.findPair(friendId, personId) != 0) {
                return true;
        }
        return false;
    }

    private List<String> friendsList(String username){
        Person person = (Person) peopleServiceImpl.loadUserByUsername(username);
        List<Long> friends = friendRepository.findAllFriends(person.getId());

        List<String> usernameOfFriends =new ArrayList<>();

        for(Long id : friends){
            Optional<Person> friend = peopleServiceImpl.findById(id);
            usernameOfFriends.add(friend.get().getUsername());
        }
        return usernameOfFriends;
    }
}
