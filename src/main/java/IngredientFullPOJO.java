import java.util.List;

public class IngredientFullPOJO {

    private String success;
    private List<IngredientPOJO> data;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<IngredientPOJO> getData() {
        return data;
    }

    public void setData(List<IngredientPOJO> data) {
        this.data = data;
    }
}
