package au.com.example.security.persistence.dao.base.query;

import java.util.List;

/**
 * QueryString
 *
 * @version $Id$
 */
public interface QueryString {
    public String getStatement();

    public List<QueryParameter> getParameters();
}
