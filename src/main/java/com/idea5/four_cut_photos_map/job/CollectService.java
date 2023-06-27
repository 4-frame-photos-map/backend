package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;
import com.idea5.four_cut_photos_map.domain.member.entity.Member;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitle;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleLog;
import com.idea5.four_cut_photos_map.domain.memberTitle.entity.MemberTitleType;
import com.idea5.four_cut_photos_map.domain.memberTitle.repository.MemberTitleLogRepository;
import com.idea5.four_cut_photos_map.domain.review.service.ReviewReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CollectService {
    private final MemberTitleLogRepository memberTitleLogRepository;
    private final FavoriteService favoriteService;
    private final ReviewReadService reviewReadService;

    // 대표 칭호 부여
    @Transactional
    public void addJoinTitle(Member member) {
        MemberTitle memberTitle = MemberTitle.builder().id(MemberTitleType.NEWBIE.getCode()).build();
        addMemberTitle(member, memberTitle, true);
    }

    // 회원에게 칭호 부여
    @Transactional
    public void addMemberTitle(Member member, MemberTitle memberTitle, Boolean isMain) {
        memberTitleLogRepository.save(new MemberTitleLog(member, memberTitle, isMain));
    }

    // 회원에게 해당 회원칭호를 부여할 수 있는지 여부
    public boolean canGiveMemberTitle(Member member, MemberTitle memberTitle) {
        // TODO: 첫번째 리뷰, 리뷰 5개 이상 기준 추가
        if(memberTitle.getId() == MemberTitleType.NEWBIE.getCode()) {
            // 1. 회원가입
            return true;
        } else if(memberTitle.getId() == MemberTitleType.FIRST_REVIEW.getCode()) {
            if(reviewReadService.getReviewCntByWriter(member) >= 1) {
                // 2. 첫번째 리뷰
                return true;
            }
        } else if(memberTitle.getId() == MemberTitleType.MANY_REVIEW.getCode()) {
            if(reviewReadService.getReviewCntByWriter(member) >= 3) {
                // 3. 리뷰 5개 이상
                return true;
            }
        } else if(memberTitle.getId() == MemberTitleType.FIRST_HEART.getCode()) {
            if(favoriteService.countByMember(member) >= 1) {
                // 4. 첫번째 찜
                return true;
            }
        } else if(memberTitle.getId() == MemberTitleType.MANY_HEART.getCode()) {
            if(favoriteService.countByMember(member) >= 3) {
                // 5. 찜 5개 이상
                return true;
            }
        }
        return false;
    }
}
