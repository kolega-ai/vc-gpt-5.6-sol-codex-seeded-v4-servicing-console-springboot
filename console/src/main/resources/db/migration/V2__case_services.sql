ALTER TABLE policy ADD COLUMN notice_template text;
UPDATE policy SET notice_template='An approval of {{amount}} GBP is waiting for your decision.' WHERE id=1;
CREATE TABLE telephone_credentials(customer_id varchar(10) primary key,salt varchar(64) not null,digest varchar(256) not null,updated_at timestamptz not null);
