package www.wemaketotem.org.totemopenhealth;

/**
 * Created by Siebren on 13-1-2017.
 */
public interface BLEDataServer extends Observer
{
    void storeData(byte[] data);
}
