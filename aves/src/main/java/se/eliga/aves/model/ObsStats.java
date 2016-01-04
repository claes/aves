package se.eliga.aves.model;

/**
 * Created by vagrant on 9/20/15.
 */
public class ObsStats {

    private String dyntaxaTaxonId;
    private String areaId;
    private int month;
    private int week;
    private int observations;
    private int observedIndividuals;

    public String getDyntaxaTaxonId() {
        return dyntaxaTaxonId;
    }

    public void setDyntaxaTaxonId(String dyntaxaTaxonId) {
        this.dyntaxaTaxonId = dyntaxaTaxonId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getObservations() {
        return observations;
    }

    public void setObservations(int observations) {
        this.observations = observations;
    }

    public int getObservedIndividuals() {
        return observedIndividuals;
    }

    public void setObservedIndividuals(int observedIndividuals) {
        this.observedIndividuals = observedIndividuals;
    }
}
