package Project.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Project.Model.Interview class with customize number of stages
 */
public class Interview implements Serializable {

    private final LinkedHashMap<String, LocalDate> stages;
    private int currentStageNum;

    Interview(String[] stageNames) {
        currentStageNum = 0;
        stages = new LinkedHashMap<>();

        for (String stage : stageNames) {
            stages.put(stage, null);
        }
    }

    public LinkedHashMap<String, LocalDate> getStages() {
        return stages;
    }

    public int getCurrentStageNum() {
        return currentStageNum;
    }

    public void setCurrentStageNum(int currentStageNum) {
        this.currentStageNum = currentStageNum;
    }

    /**
     * get the date of the stage
     */
    LocalDate getStageDate(int stageNum) {
        String stage = getStage(stageNum);
        return getStageDate(stage);
    }

    /**
     * Since LinkedHashMap stages is ordered, by that we can find the stage name by the number of the index
     *
     * @return Stage name that is used in stages
     */
    public String getStage(int stageNum) {
        return new ArrayList<>(stages.keySet()).get(stageNum);
    }

    // Method overloading
    public LocalDate getStageDate(String stage) {
        return stages.get(stage);
    }

    void setStageDate(int stageNum, LocalDate date) {
        String stage = getStage(stageNum);
        setStageDate(stage, date);
    }

    // Method overloading
    private void setStageDate(String stage, LocalDate date) {
        stages.replace(stage, date);
    }

}
