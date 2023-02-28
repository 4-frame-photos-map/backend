-- insert into memberTitle (create_date, modify_date, name) values(NOW(), NOW(), '뉴비');
-- insert into memberTitle (create_date, modify_date, name) values(NOW(), NOW(), '포토부스 리뷰어'); -- 리뷰
-- insert into memberTitle (create_date, modify_date, name) values(NOW(), NOW(), '포토부스 전문가'); -- 방문횟수

INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt)VALUES (NOW(), NOW(), '서울 성동구 서울숲2길 48', '인생네컷 서울숲노가리마트로드점', '인생네컷', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '서울 성동구 서울숲2길 17-2', '포토이즘박스 성수점', '포토이즘박스', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '서울 성동구 서울숲4길 13', '인생네컷 카페성수로드점', '인생네컷', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '서울 성동구 서울숲2길 45', '하루필름 서울숲점', '하루필름', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '서울 성동구 서울숲4길 20', '인생네컷 서울숲점', '인생네컷', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '서울 성동구 서울숲4길 23-1', '픽닷', '픽닷', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '충남 천안시 서북구 원두정2길 21', '인생네컷 충남천안두정먹거리공원점', '인생네컷', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '충남 천안시 동남구 먹거리10길 14', '하루필름 천안점', '하루필름', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '충남 천안시 서북구 원두정2길 21', '포토이즘박스 두정점', '포토이즘박스', 0);
INSERT INTO SHOP(create_date, modify_date, road_address_name, place_name, brand, favorite_cnt) VALUES(NOW(), NOW(), '충남 천안시 동남구 먹거리11길 28', '포토이즘컬러드 천안신부점', '포토이즘박스', 0);


-- 칭호
INSERT INTO MEMBER_TITLE(create_date, modify_date, name, content) VALUES (NOW(), NOW(), '뉴비', '네컷지도 가입');
INSERT INTO MEMBER_TITLE(create_date, modify_date, name, content) VALUES (NOW(), NOW(), '리뷰 첫 걸음', '리뷰 1회 누적');
INSERT INTO MEMBER_TITLE(create_date, modify_date, name, content) VALUES (NOW(), NOW(), '리뷰 홀릭', '리뷰 5회 누적');
INSERT INTO MEMBER_TITLE(create_date, modify_date, name, content) VALUES (NOW(), NOW(), '찜 첫 걸음', '찜 1회 누적');
INSERT INTO MEMBER_TITLE(create_date, modify_date, name, content) VALUES (NOW(), NOW(), '찜 홀릭', '찜 5회 누적');