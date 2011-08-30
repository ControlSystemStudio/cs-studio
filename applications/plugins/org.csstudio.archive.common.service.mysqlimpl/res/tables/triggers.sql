DROP TRIGGER IF EXISTS updateLastSampleTime//

CREATE TRIGGER updateLastSampleTime 
        BEFORE INSERT ON sample 
        FOR EACH ROW 
            BEGIN 
                UPDATE channel set last_sample_time=NEW.time where id=NEW.channel_id;
            END//
