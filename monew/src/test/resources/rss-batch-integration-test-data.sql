INSERT INTO keywords (id, name, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'AI', NOW(), NOW());

INSERT INTO interests (id, name, subscriber_count, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111110', 'AI Interest', 0, NOW(), NOW());

INSERT INTO interest_keywords (id, interest_id, keyword_id, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111100', '11111111-1111-1111-1111-111111111110',
        '11111111-1111-1111-1111-111111111111', NOW(), NOW());

INSERT INTO dummy_articles (id, title, source, source_url, summary, view_count, published_date,
                            deleted)
values ('11111111-1111-1111-1111-111111111000', 'AI IA AI', 'NAVER', 'http://test.com',
        'AI AI AI AI', 0, NOW(), false);

UPDATE dummy_articles
SET body_tsv = to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(summary, ''))
WHERE body_tsv IS NULL;
