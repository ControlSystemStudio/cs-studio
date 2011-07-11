DROP TRIGGER IF EXISTS updateLastSampleTime//

CREATE DEFINER='root'@'localhost' 
    TRIGGER updateLastSampleTime 
        BEFORE INSERT ON sample 
        FOR EACH ROW 
            BEGIN 
                UPDATE channel set last_sample_time=NEW.sample_time where id=NEW.channel_id;
            END//
