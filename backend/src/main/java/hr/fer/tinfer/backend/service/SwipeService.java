package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.SwipeResponse;
import hr.fer.tinfer.backend.model.Conversation;
import hr.fer.tinfer.backend.model.ConversationParticipant;
import hr.fer.tinfer.backend.model.Match;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.model.DatingSwipe;
import hr.fer.tinfer.backend.repository.ConversationRepository;
import hr.fer.tinfer.backend.repository.DatingSwipeRepository;
import hr.fer.tinfer.backend.repository.MatchRepository;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import hr.fer.tinfer.backend.types.SwipeAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SwipeService {

    private final ProfileRepository profileRepository;
    private final DatingSwipeRepository datingSwipeRepository;
    private final MatchRepository matchRepository;
    private final ConversationRepository conversationRepository;

    public SwipeResponse swipe(UUID swiperId, UUID swipedId, SwipeAction action) {
        if (action == null) {
            throw new IllegalArgumentException("Swipe action must be provided");
        }
        if (swiperId.equals(swipedId)) {
            throw new IllegalArgumentException("You cannot swipe on your own profile");
        }

        Profile swiper = profileRepository.findById(swiperId)
                .orElseThrow(() -> new IllegalArgumentException("Swiper profile not found"));
        Profile swiped = profileRepository.findById(swipedId)
                .orElseThrow(() -> new IllegalArgumentException("Target profile not found"));

        validateActive(swiper);
        validateActive(swiped);

        DatingSwipe swipe = datingSwipeRepository.findBySwiperAndSwiped(swiper, swiped)
                .map(existing -> {
                    existing.setAction(action);
                    return existing;
                })
                .orElseGet(() -> {
                    DatingSwipe fresh = new DatingSwipe();
                    fresh.setSwiper(swiper);
                    fresh.setSwiped(swiped);
                    fresh.setAction(action);
                    return fresh;
                });

        datingSwipeRepository.save(swipe);
        log.debug("{} swiped {} with action {}", swiperId, swipedId, action);

        SwipeResponse.SwipeResponseBuilder responseBuilder = SwipeResponse.builder()
                .swipedUserId(swipedId)
                .action(action)
                .matchCreated(false);

        if (isPositiveAction(action)) {
            Optional<DatingSwipe> reverseSwipe = datingSwipeRepository.findBySwiperAndSwiped(swiped, swiper);
            if (reverseSwipe.isPresent() && isPositiveAction(reverseSwipe.get().getAction())) {
                Match match = matchRepository.findByUsers(swiperId, swipedId)
                        .orElseGet(() -> createMatch(swiper, swiped));

                responseBuilder.matchCreated(true).matchId(match.getId());

                if (match.getConversation() == null) {
                    Conversation conversation = createConversation(match, swiper, swiped);
                    match.setConversation(conversation);
                    matchRepository.save(match);
                }

                Long conversationId = match.getConversation() != null ? match.getConversation().getId() : null;
                responseBuilder.conversationId(conversationId);
            }
        }

        return responseBuilder.build();
    }

    private Match createMatch(Profile userA, Profile userB) {
        Profile first = userA.getId().compareTo(userB.getId()) < 0 ? userA : userB;
        Profile second = first.getId().equals(userA.getId()) ? userB : userA;

        Match match = new Match();
        match.setUser1(first);
        match.setUser2(second);

        Match savedMatch = matchRepository.save(match);
        Conversation conversation = createConversation(savedMatch, userA, userB);
        savedMatch.setConversation(conversation);
        return matchRepository.save(savedMatch);
    }

    private Conversation createConversation(Match match, Profile userA, Profile userB) {
        Conversation conversation = new Conversation();
        conversation.setMatch(match);
        conversation.setParticipants(new HashSet<>());

        addParticipant(conversation, userA);
        addParticipant(conversation, userB);

        return conversationRepository.save(conversation);
    }

    private void addParticipant(Conversation conversation, Profile user) {
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(user);
        participant.setLastReadAt(LocalDateTime.now());
        participant.setIsMuted(false);

        conversation.getParticipants().add(participant);
    }

    private void validateActive(Profile profile) {
        if (!Boolean.TRUE.equals(profile.getIsActive())) {
            throw new IllegalStateException("Profile " + profile.getId() + " is inactive");
        }
    }

    private boolean isPositiveAction(SwipeAction action) {
        return action == SwipeAction.LIKE || action == SwipeAction.SUPER_LIKE;
    }
}
