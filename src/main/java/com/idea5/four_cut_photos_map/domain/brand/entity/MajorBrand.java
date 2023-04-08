package com.idea5.four_cut_photos_map.domain.brand.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MajorBrand {
    LIFEFOURCUTS("인생네컷", "https://file.notion.so/f/s/26ffda84-0a89-46f1-9503-0577f63402cd/Group_4.jpg?id=ebac4e34-3e98-4d91-a7df-daf77f60d414&table=block&spaceId=24a7174d-7092-4384-81e6-6d02373055df&expirationTimestamp=1681032927437&signature=1rJRDhiy1J2VJ4WajMqiO1fzfdKnBdgqlM2S6CYCgv8&downloadName=Group+4.jpg"),
    HARUFILM("하루필름", "https://file.notion.so/f/s/a7ca9934-1376-46bb-be18-4d39bd616958/Group_3.jpg?id=ca43da52-7eed-4d0a-ad3d-7f326e92a701&table=block&spaceId=24a7174d-7092-4384-81e6-6d02373055df&expirationTimestamp=1681032910051&signature=UuZRuLlcljwevWMMMzIsDQTWatDW6op0lr2vBYRpZSU&downloadName=Group+3.jpg"),
    PHOTOISM("포토이즘", "https://file.notion.so/f/s/cf5c7073-a220-41ad-8bf7-69597c79fe8c/Group_1.jpg?id=a41b3413-18ec-4f09-bfe2-618709b2789b&table=block&spaceId=24a7174d-7092-4384-81e6-6d02373055df&expirationTimestamp=1681032939815&signature=GA1_0IlsH6G89tgb_XwYFD-g6Kf0D7khG1wovnoHVHI&downloadName=Group+1.jpg"),
    PHOTOGRAY("포토그레이", "https://file.notion.so/f/s/ec6cb63f-45f3-4801-acc1-66b0390ee348/Group_2.jpg?id=d6aed934-e2d9-4e5e-83d2-b6dcf3d5bd6c&table=block&spaceId=24a7174d-7092-4384-81e6-6d02373055df&expirationTimestamp=1681031801816&signature=tanetGnvODfoBa0wSCGruOgsFPUXxJSefGp00aZnfNU&downloadName=Group+2.jpg");

    private String brandName;
    private String filePath;
}
