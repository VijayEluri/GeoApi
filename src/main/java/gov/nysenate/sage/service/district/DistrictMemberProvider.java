package gov.nysenate.sage.service.district;

import gov.nysenate.sage.dao.model.AssemblyDao;
import gov.nysenate.sage.dao.model.CongressionalDao;
import gov.nysenate.sage.dao.model.SenateDao;
import gov.nysenate.sage.model.district.DistrictInfo;
import gov.nysenate.sage.model.district.DistrictMap;
import gov.nysenate.sage.model.district.DistrictOverlap;
import gov.nysenate.sage.model.result.DistrictResult;
import gov.nysenate.sage.model.result.MapResult;
import gov.nysenate.services.model.Senator;

import java.util.HashMap;
import java.util.Map;

import static gov.nysenate.sage.model.district.DistrictType.*;

/**
 * Typically when the district service providers return a DistrictInfo, only the district codes
 * and maps are provided. This class provides methods to populate the remaining data which includes
 * the district members and the senator information. Since this information is not always required, this
 * functionality should be invoked through a controller as opposed to the provider implementations.
 */
public abstract class DistrictMemberProvider
{
    /**
     * Sets the senator, congressional, and assembly member data to the district result.
     * @param districtResult
     */
    public static void assignDistrictMembers(DistrictResult districtResult)
    {
        SenateDao senateDao = new SenateDao();

        /** Proceed on either a success or partial result */
        if (districtResult.isSuccess()) {
            DistrictInfo districtInfo = districtResult.getDistrictInfo();
            if (districtInfo != null) {
                /** Set the Senate, Congressional, and Assembly data using the respective daos */
                if (districtInfo.hasDistrictCode(SENATE)) {
                    int senateCode = Integer.parseInt(districtInfo.getDistCode(SENATE));
                    districtInfo.setSenator(senateDao.getSenatorByDistrict(senateCode));
                }
                if (districtInfo.hasDistrictCode(CONGRESSIONAL)) {
                    int congressionalCode = Integer.parseInt(districtInfo.getDistCode(CONGRESSIONAL));
                    districtInfo.setDistrictMember(CONGRESSIONAL, new CongressionalDao().getCongressionalByDistrict(congressionalCode));
                }
                if (districtInfo.hasDistrictCode(ASSEMBLY)) {
                    int assemblyCode = Integer.parseInt(districtInfo.getDistCode(ASSEMBLY));
                    districtInfo.setDistrictMember(ASSEMBLY, new AssemblyDao().getAssemblyByDistrict(assemblyCode));
                }

                /** Fill in neighbor district senator info */
                for (DistrictMap districtMap : districtInfo.getNeighborMaps(SENATE)) {
                    districtMap.setSenator(senateDao.getSenatorByDistrict(Integer.parseInt(districtMap.getDistrictCode())));
                }

                /** Fill in senator members if overlap exists */
                DistrictOverlap senateOverlap = districtInfo.getDistrictOverlap(SENATE);
                if (senateOverlap != null) {
                    Map<String, Senator> senatorMap = new HashMap<>();
                    for (String district : senateOverlap.getTargetDistricts()) {
                        Senator senator = senateDao.getSenatorByDistrict(Integer.parseInt(district));
                        senatorMap.put(district, senator);
                    }
                    senateOverlap.setTargetSenators(senatorMap);
                }

                districtResult.setDistrictInfo(districtInfo);
            }
        }
    }

    /**
     * Sets the senator, congressional, and assembly member data to the map result.
     * @param mapResult
     */
    public static void assignDistrictMembers(MapResult mapResult)
    {
        if (mapResult != null && mapResult.isSuccess()) {
            for (DistrictMap map : mapResult.getDistrictMaps()) {
                if (map.getDistrictType().equals(SENATE)) {
                    int senateCode = Integer.parseInt(map.getDistrictCode());
                    map.setSenator(new SenateDao().getSenatorByDistrict(senateCode));
                }
                else if (map.getDistrictType().equals(CONGRESSIONAL)) {
                    int congressionalCode = Integer.parseInt(map.getDistrictCode());
                    map.setMember(new CongressionalDao().getCongressionalByDistrict(congressionalCode));
                }
                else if (map.getDistrictType().equals(ASSEMBLY)) {
                    int assemblyCode = Integer.parseInt(map.getDistrictCode());
                    map.setMember(new AssemblyDao().getAssemblyByDistrict(assemblyCode));
                }
            }
        }
    }
}
