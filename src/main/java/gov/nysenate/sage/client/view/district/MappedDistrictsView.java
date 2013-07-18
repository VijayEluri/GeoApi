package gov.nysenate.sage.client.view.district;

import gov.nysenate.sage.model.district.DistrictInfo;

import static gov.nysenate.sage.model.district.DistrictType.*;
import static gov.nysenate.sage.model.district.DistrictType.VILLAGE;
import static gov.nysenate.sage.model.district.DistrictType.WARD;

public class MappedDistrictsView
{
    protected MappedSenateDistrictView senate;
    protected MappedMemberDistrictView congressional;
    protected MappedMemberDistrictView assembly;
    protected MappedDistrictView county;
    protected MappedDistrictView election;
    protected MappedDistrictView school;
    protected MappedDistrictView town;
    protected MappedDistrictView zip;
    protected MappedDistrictView cleg;
    protected MappedDistrictView ward;
    protected MappedDistrictView village;

    public MappedDistrictsView(DistrictInfo dInfo)
    {
        if (dInfo != null) {
            this.senate = new MappedSenateDistrictView(dInfo, dInfo.getSenator());
            this.congressional = new MappedMemberDistrictView(CONGRESSIONAL, dInfo, dInfo.getDistrictMember(CONGRESSIONAL));
            this.assembly = new MappedMemberDistrictView(ASSEMBLY, dInfo, dInfo.getDistrictMember(ASSEMBLY));
            this.county = new MappedDistrictView(COUNTY, dInfo);
            this.election = new MappedDistrictView(ELECTION, dInfo);
            this.school = new MappedDistrictView(SCHOOL, dInfo);
            this.town = new MappedDistrictView(TOWN, dInfo);
            this.zip = new MappedDistrictView(ZIP, dInfo);
            this.cleg = new MappedDistrictView(CLEG, dInfo);
            this.ward = new MappedDistrictView(WARD, dInfo);
            this.village = new MappedDistrictView(VILLAGE, dInfo);
        }
    }

    public MappedSenateDistrictView getSenate() {
        return (senate != null && senate.district != null) ? senate : null;
    }

    public MappedMemberDistrictView getCongressional() {
        return (congressional != null && congressional.district != null) ? congressional : null;
    }

    public MappedMemberDistrictView getAssembly() {
        return (assembly != null && assembly.district != null) ? assembly : null;
    }

    public MappedDistrictView getCounty() {
        return (county != null && county.district != null) ? county : null;
    }

    public MappedDistrictView getElection() {
        return (election != null && election.district != null) ? election : null;
    }

    public MappedDistrictView getSchool() {
        return (school != null && school.district != null) ? school : null;
    }

    public MappedDistrictView getTown() {
        return (town != null && town.district != null) ? town : null;
    }

    public MappedDistrictView getZip() {
        return (zip != null && zip.district != null) ? zip : null;
    }

    public MappedDistrictView getCleg() {
        return (cleg != null && cleg.district != null) ? cleg : null;
    }

    public MappedDistrictView getWard() {
        return (ward != null && ward.district != null) ? ward : null;
    }

    public MappedDistrictView getVillage() {
        return (village != null && village.district != null) ? village : null;
    }
}
