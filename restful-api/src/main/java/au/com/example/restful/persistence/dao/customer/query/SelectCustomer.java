package au.com.example.restful.persistence.dao.customer.query;

import au.com.example.security.persistence.dao.base.query.QueryParameter;
import au.com.example.security.persistence.dao.base.query.QueryString;

import java.util.ArrayList;
import java.util.List;

public class SelectCustomer implements QueryString {
    private static final String QUERY = "SELECT c FROM " + "CustomerEntity" + " c WHERE (1=1) ";

    private Long id;

    public SelectCustomer(Long id) {
        this.id = id;
    }

    // === QueryString implementation

    public String getStatement() {
        StringBuffer statement = new StringBuffer(QUERY);

        if (id != null) {
            statement.append("AND (c.id = :id) ");
        }

        return statement.toString();
    }

    public List<QueryParameter> getParameters() {
        List<QueryParameter> parameters = new ArrayList<>();

        if (id != null) {
            parameters.add(new QueryParameter("id", id));
        }

        return parameters;
    }
}