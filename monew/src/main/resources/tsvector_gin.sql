ALTER TABLE dummy_articles ADD COLUMN IF NOT EXISTS body_tsv tsvector;

CREATE INDEX dummy_articles_body_gin ON dummy_articles USING gin(body_tsv);

CREATE OR REPLACE FUNCTION dummy_tsv_trigger()
RETURNS trigger AS $$
BEGIN
    NEW.body_tsv :=
        to_tsvector('simple', coalesce(NEW.title,'') || ' ' || coalesce(NEW.summary, ''));
    RETURN NEW;
END $$ LANGUAGE plpgsql;


ALTER TABLE articles ADD COLUMN IF NOT EXISTS body_tsv tsvector;

CREATE INDEX articles_body_gin ON articles USING gin(body_tsv);

CREATE OR REPLACE FUNCTION articles_tsv_trigger()
RETURNS trigger AS $$
BEGIN
  NEW.body_tsv :=
    to_tsvector(
      'simple',
      coalesce(NEW.title,'') || ' ' || coalesce(NEW.summary,'')
    );
RETURN NEW;
END $$ LANGUAGE plpgsql;


CREATE TRIGGER dummy_tsv_trg
    BEFORE INSERT OR UPDATE ON dummy_articles
    FOR EACH ROW EXECUTE FUNCTION dummy_tsv_trigger();

CREATE TRIGGER article_tsv_trg
    BEFORE INSERT OR UPDATE ON articles
                         FOR EACH ROW
                         WHEN (NEW.body_tsv IS NULL)
                         EXECUTE FUNCTION articles_tsv_trigger();

UPDATE articles
SET    body_tsv = to_tsvector('simple', coalesce(title,'') || ' ' || coalesce(summary,''))
WHERE  body_tsv IS NULL;

ALTER TABLE interest_keywords
    ADD CONSTRAINT uq_interest_keyword UNIQUE (interest_id, keyword_id);
