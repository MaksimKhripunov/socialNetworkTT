package ru.khripunov.socialnetworktt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Friend;
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.repository.FriendRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final PeopleService peopleService;
    public void sendInvite(String recipient, Jwt principal) {
        String username = peopleService.checkJWT(principal);
        Friend friend = new Friend();
        Person person = (Person) peopleService.loadUserByUsername(username);
        Person rec = (Person) peopleService.loadUserByUsername(recipient);
        friend.setPersonId(person.getId());
        friend.setFriendId(rec.getId());

        if(friendRepository.findPair(person.getId(), rec.getId()) != 0)
            return;

        friendRepository.save(friend);
    }


    public List<String> listOfFriends(Jwt principal) {
        String username = peopleService.checkJWT(principal);
        Person person = (Person) peopleService.loadUserByUsername(username);

        return friendsList(person.getUsername());

    }

    public List<String> listOfFriends(String username) {
        Person person = (Person) peopleService.loadUserByUsername(username);
        if(person.getHideFriendsList())
            return new ArrayList<>();
        return friendsList(username);
    }

    public boolean checkRelationships(Long personId, Long friendId) {
        if(friendRepository.findPair(personId, friendId) != 0 && friendRepository.findPair(friendId, personId) != 0) {
                return true;
        }
        return false;
    }

    private List<String> friendsList(String username){
        Person person = (Person) peopleService.loadUserByUsername(username);
        List<Long> friends = friendRepository.findAllFriends(person.getId());

        List<String> usernameOfFriends =new ArrayList<>();

        for(Long id : friends){
            Optional<Person> friend = peopleService.findById(id);
            usernameOfFriends.add(friend.get().getUsername());
        }
        return usernameOfFriends;
    }
}
