package cupitoo.wtwt.service;

import cupitoo.wtwt.controller.review.ReviewDto;
import cupitoo.wtwt.model.group.Group;
import cupitoo.wtwt.model.user.User;
import cupitoo.wtwt.model.review.PersonalityReview;
import cupitoo.wtwt.model.review.Review;
import cupitoo.wtwt.model.review.StyleReview;
import cupitoo.wtwt.repository.*;
import cupitoo.wtwt.repository.group.GroupRepository;
import cupitoo.wtwt.repository.review.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ReviewRepository reviewRepository;
    private final PersonalityRepository personalityRepository;
    private final PersonalityReviewRepository personalityReviewRepository;
    private final StyleRepository styleRepository;
    private final StyleReviewRepository styleReviewRepository;

    /**
     * 리뷰 생성
     */
    @Transactional
    public Long createReview(Long userId, Long groupId, ReviewDto reviewDto) throws IllegalAccessException {
        User sender = userRepository.findById(userId).get();
        Group group = groupRepository.findById(groupId).get();

        validatePermission(sender, group);
        log.debug("ReceiverId: " + reviewDto.getReceiverId());
        User receiver = userRepository.findById(reviewDto.getReceiverId()).get();
        Review review = Review.builder()
                .rate(reviewDto.getRate())
                .receiver(receiver)
                .sender(sender)
                .group(group)
                .comment(reviewDto.getComment())
                .build();
        reviewRepository.save(review);

        receiver.updateRate(reviewRepository.getAverageRateByReceiver(receiver));

        for (Long id : reviewDto.getPersonalities()) {
            PersonalityReview pr = new PersonalityReview(review, personalityRepository.findById(id).get());
            personalityReviewRepository.save(pr);
        }

        for (Long id : reviewDto.getStyles()) {
            StyleReview sr = new StyleReview(review, styleRepository.findById(id).get());
            styleReviewRepository.save(sr);
        }

        return review.getId();
    }
    private void validatePermission(User user, Group group) throws IllegalAccessException {
        if(!groupRepository.existsUserInGroup(group, user)) {
            throw new IllegalAccessException("리뷰 권한이 없습니다.");
        }
    }
}
