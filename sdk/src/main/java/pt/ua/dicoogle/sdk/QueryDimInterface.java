package pt.ua.dicoogle.sdk;

import pt.ua.dicoogle.sdk.QueryInterface;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.datastructs.dim.Patient;
import pt.ua.dicoogle.sdk.datastructs.dim.Serie;
import pt.ua.dicoogle.sdk.datastructs.dim.Study;

/**
 * Created by bastiao on 02-02-2017.
 */
public interface QueryDimInterface extends QueryInterface {

    public Iterable<Patient> queryPatient(String query, Object ... parameters);
    public Iterable<Study> queryStudy(String query, Object ... parameters);
    public Iterable<Serie> querySeries(String query, Object ... parameters);

}
