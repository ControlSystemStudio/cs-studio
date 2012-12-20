DROP TRIGGER IF EXISTS insertLastSampleRow//

CREATE TRIGGER insertLastSampleRow 
        AFTER INSERT ON channel 
        FOR EACH ROW 
            BEGIN 
                INSERT INTO last_sample (channel_id) VALUES (NEW.id);
            END//


DROP TRIGGER IF EXISTS updateLastSampleTime//

CREATE TRIGGER updateLastSampleTime
        BEFORE INSERT ON sample 
        FOR EACH ROW 
            BEGIN
                UPDATE last_sample SET time=NEW.time WHERE channel_id=NEW.channel_id;
            END//


DROP TRIGGER IF EXISTS updateLastSampleBlobTime//
            
CREATE TRIGGER updateLastSampleBlobTime
        BEFORE INSERT ON sample_blob 
        FOR EACH ROW 
            BEGIN 
                UPDATE last_sample SET time=NEW.time WHERE channel_id=NEW.channel_id;
            END//
