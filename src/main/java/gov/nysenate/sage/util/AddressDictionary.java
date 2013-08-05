package gov.nysenate.sage.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains maps for resolving common address component names.
 */
public class AddressDictionary
{
    public static final Map<String, String> stateMap = new HashMap<>();
    public static final Map<String, String> unitMap = new HashMap<>();
    public static final Map<String, String> streetTypeMap = new HashMap<>();
    public static final Map<String, String> highWayMap = new HashMap<>();
    public static final Map<String, String> directionMap = new HashMap<>();
    public static final Map<String, String> streetPrefixMap = new HashMap<>();

    /* State mappings */
    static {
        stateMap.put("AL","Alabama");
        stateMap.put("AK","Alaska");
        stateMap.put("AS","American Samoa");
        stateMap.put("AZ","Arizona");
        stateMap.put("AR","Arkansas");
        stateMap.put("CA","California");
        stateMap.put("CO","Colorado");
        stateMap.put("CT","Connecticut");
        stateMap.put("DE","Delaware");
        stateMap.put("DC","District of Columbia");
        stateMap.put("FM","Federated States of Micronesia");
        stateMap.put("FL","Florida");
        stateMap.put("GA","Georgia");
        stateMap.put("GU","Guam");
        stateMap.put("HI","Hawaii");
        stateMap.put("ID","Idaho");
        stateMap.put("IL","Illinois");
        stateMap.put("IN","Indiana");
        stateMap.put("IA","Iowa");
        stateMap.put("KS","Kansas");
        stateMap.put("KY","Kentucky");
        stateMap.put("LA","Louisiana");
        stateMap.put("ME","Maine");
        stateMap.put("MH","Marshall Islands");
        stateMap.put("MD","Maryland");
        stateMap.put("MA","Massachusetts");
        stateMap.put("MI","Michigan");
        stateMap.put("MN","Minnesota");
        stateMap.put("MS","Mississippi");
        stateMap.put("MO","Missouri");
        stateMap.put("MT","Montana");
        stateMap.put("NE","Nebraska");
        stateMap.put("NV","Nevada");
        stateMap.put("NH","New Hampshire");
        stateMap.put("NJ","New Jersey");
        stateMap.put("NM","New Mexico");
        stateMap.put("NY","New York");
        stateMap.put("NC","North Carolina");
        stateMap.put("ND","North Dakota");
        stateMap.put("MP","Northern Mariana Islands");
        stateMap.put("OH","Ohio");
        stateMap.put("OK","Oklahoma");
        stateMap.put("OR","Oregon");
        stateMap.put("PW","Palau");
        stateMap.put("PA","Pennsylvania");
        stateMap.put("PR","Puerto Rico");
        stateMap.put("RI","Rhode Island");
        stateMap.put("SC","South Carolina");
        stateMap.put("SD","South Dakota");
        stateMap.put("TN","Tennessee");
        stateMap.put("TX","Texas");
        stateMap.put("UT","Utah");
        stateMap.put("VT","Vermont");
        stateMap.put("VI","Virgin Islands");
        stateMap.put("VA","Virginia");
        stateMap.put("WA","Washington");
        stateMap.put("WV","West Virginia");
        stateMap.put("WI","Wisconsin");
        stateMap.put("WY","Wyoming");
    }

    /* Unit mappings */
    static {
        unitMap.put("APARTMENT","APT");
        unitMap.put("APT","APT");
        unitMap.put("BASEMENT","BSMT");
        unitMap.put("BSMT","BSMT");
        unitMap.put("BUILDING","BLDG");
        unitMap.put("BLDG","BLDG");
        unitMap.put("DEPARTMENT","DEPT");
        unitMap.put("DEPT","DEPT");
        unitMap.put("FLOOR","FL");
        unitMap.put("FL","FL");
        unitMap.put("FRONT","FRNT");
        unitMap.put("FRNT","FRNT");
        unitMap.put("HANGAR","HNGR");
        unitMap.put("HNGR","HNGR");
        unitMap.put("LOBBY","LBBY");
        unitMap.put("LBBY","LBBY");
        unitMap.put("LOT","LOT");
        unitMap.put("LOWER","LOWR");
        unitMap.put("LOWR","LOWR");
        unitMap.put("OFFICE","OFC");
        unitMap.put("OFC","OFC");
        unitMap.put("PENTHOUSE","PH");
        unitMap.put("PH","PH");
        unitMap.put("PIER","PIER");
        unitMap.put("REAR","REAR");
        unitMap.put("ROOM","RM");
        unitMap.put("RM","RM");
        unitMap.put("SIDE","SIDE");
        unitMap.put("SLIP","SLIP");
        unitMap.put("SPACE","SPC");
        unitMap.put("SPC","SPC");
        unitMap.put("STOP","STOP");
        unitMap.put("SUITE","STE");
        unitMap.put("STE","STE");
        unitMap.put("TRAILER","TRLR");
        unitMap.put("TRLR","TRLR");
        unitMap.put("UNIT","UNIT");
        unitMap.put("UPPER","UPPR");
        unitMap.put("UPPR","UPPR");
    }

    /* Street type mappings */
    static {
        streetTypeMap.put("ALLEE","Aly");
        streetTypeMap.put("ALLEY","Aly");
        streetTypeMap.put("ALLY","Aly");
        streetTypeMap.put("ALY","Aly");
        streetTypeMap.put("ANEX","Anx");
        streetTypeMap.put("ANNEX","Anx");
        streetTypeMap.put("ANNX","Anx");
        streetTypeMap.put("ANX","Anx");
        streetTypeMap.put("ARC","Arc");
        streetTypeMap.put("ARCADE","Arc");
        streetTypeMap.put("AV","Ave");
        streetTypeMap.put("AVE","Ave");
        streetTypeMap.put("AVEN","Ave");
        streetTypeMap.put("AVENU","Ave");
        streetTypeMap.put("AVENUE","Ave");
        streetTypeMap.put("AVN","Ave");
        streetTypeMap.put("AVNUE","Ave");
        streetTypeMap.put("BAYOO","Byu");
        streetTypeMap.put("BAYOU","Byu");
        streetTypeMap.put("BCH","Bch");
        streetTypeMap.put("BEACH","Bch");
        streetTypeMap.put("BEND","Bnd");
        streetTypeMap.put("BND","Bnd");
        streetTypeMap.put("BLF","Blf");
        streetTypeMap.put("BLUF","Blf");
        streetTypeMap.put("BLUFF","Blf");
        streetTypeMap.put("BLUFFS","Blfs");
        streetTypeMap.put("BOT","Btm");
        streetTypeMap.put("BOTTM","Btm");
        streetTypeMap.put("BOTTOM","Btm");
        streetTypeMap.put("BTM","Btm");
        streetTypeMap.put("BLVD","Blvd");
        streetTypeMap.put("BOUL","Blvd");
        streetTypeMap.put("BOULEVARD","Blvd");
        streetTypeMap.put("BOULV","Blvd");
        streetTypeMap.put("BR","Br");
        streetTypeMap.put("BRANCH","Br");
        streetTypeMap.put("BRNCH","Br");
        streetTypeMap.put("BRDGE","Brg");
        streetTypeMap.put("BRG","Brg");
        streetTypeMap.put("BRIDGE","Brg");
        streetTypeMap.put("BRK","Brk");
        streetTypeMap.put("BROOK","Brk");
        streetTypeMap.put("BROOKS","Brks");
        streetTypeMap.put("BURG","Bg");
        streetTypeMap.put("BURGS","Bgs");
        streetTypeMap.put("BYP","Byp");
        streetTypeMap.put("BYPA","Byp");
        streetTypeMap.put("BYPAS","Byp");
        streetTypeMap.put("BYPASS","ByP");
        streetTypeMap.put("BYPS","Byp");
        streetTypeMap.put("CAMP","Cp");
        streetTypeMap.put("CMP","Cp");
        streetTypeMap.put("CP","Cp");
        streetTypeMap.put("CANYN","Cyn");
        streetTypeMap.put("CANYON","Cyn");
        streetTypeMap.put("CNYN","Cyn");
        streetTypeMap.put("CYN","Cyn");
        streetTypeMap.put("CAPE","Cpe");
        streetTypeMap.put("CPE","Cpe");
        streetTypeMap.put("CAUSEWAY","Cswy");
        streetTypeMap.put("CAUSWAY","Cswy");
        streetTypeMap.put("CSWY","Cswy");
        streetTypeMap.put("CEN","Ctr");
        streetTypeMap.put("CENT","Ctr");
        streetTypeMap.put("CENTER","Ctr");
        streetTypeMap.put("CENTR","Ctr");
        streetTypeMap.put("CENTRE","Ctr");
        streetTypeMap.put("CNTER","Ctr");
        streetTypeMap.put("CNTR","Ctr");
        streetTypeMap.put("CTR","Ctr");
        streetTypeMap.put("CENTERS","Ctrs");
        streetTypeMap.put("CIR","Cir");
        streetTypeMap.put("CIRC","Cir");
        streetTypeMap.put("CIRCL","Cir");
        streetTypeMap.put("CIRCLE","Cir");
        streetTypeMap.put("CRCL","Cir");
        streetTypeMap.put("CRCLE","Cir");
        streetTypeMap.put("CIRCLES","Cirs");
        streetTypeMap.put("CLF","Clf");
        streetTypeMap.put("CLIFF","Clf");
        streetTypeMap.put("CLFS","Clfs");
        streetTypeMap.put("CLIFFS","Clfs");
        streetTypeMap.put("CLB","Clb");
        streetTypeMap.put("CLUB","Clb");
        streetTypeMap.put("COMMON","Cmn");
        streetTypeMap.put("COR","Cor");
        streetTypeMap.put("CORNER","Cor");
        streetTypeMap.put("CORNERS","Cors");
        streetTypeMap.put("CORS","Cors");
        streetTypeMap.put("COURSE","Crse");
        streetTypeMap.put("CRSE","Crse");
        streetTypeMap.put("COURT","Ct");
        streetTypeMap.put("CRT","Ct");
        streetTypeMap.put("CT","Ct");
        streetTypeMap.put("COURTS","Cts");
        streetTypeMap.put("COVE","Cv");
        streetTypeMap.put("CV","Cv");
        streetTypeMap.put("COVES","Cvs");
        streetTypeMap.put("CK","Crk");
        streetTypeMap.put("CR","Crk");
        streetTypeMap.put("CREEK","Crk");
        streetTypeMap.put("CRK","Crk");
        streetTypeMap.put("CRECENT","Cres");
        streetTypeMap.put("CRES","Cres");
        streetTypeMap.put("CRESCENT","Cres");
        streetTypeMap.put("CRESENT","Cres");
        streetTypeMap.put("CRSCNT","Cres");
        streetTypeMap.put("CRSENT","Cres");
        streetTypeMap.put("CRSNT","Cres");
        streetTypeMap.put("CREST","Crst");
        streetTypeMap.put("CROSSING","Xing");
        streetTypeMap.put("CRSSING","Xing");
        streetTypeMap.put("CRSSNG","Xing");
        streetTypeMap.put("XING","Xing");
        streetTypeMap.put("CROSSROAD","Xrd");
        streetTypeMap.put("CURVE","Curv");
        streetTypeMap.put("DALE","Dl");
        streetTypeMap.put("DL","Dl");
        streetTypeMap.put("DAM","Dm");
        streetTypeMap.put("DM","Dm");
        streetTypeMap.put("DIV","Dv");
        streetTypeMap.put("DIVIDE","Dv");
        streetTypeMap.put("DV","Dv");
        streetTypeMap.put("DVD","Dv");
        streetTypeMap.put("DR","Dr");
        streetTypeMap.put("DRIV","Dr");
        streetTypeMap.put("DRIVE","Dr");
        streetTypeMap.put("DRV","Dr");
        streetTypeMap.put("DRIVES","Drs");
        streetTypeMap.put("EST","Est");
        streetTypeMap.put("ESTATE","Est");
        streetTypeMap.put("ESTATES","Ests");
        streetTypeMap.put("ESTS","Ests");
        streetTypeMap.put("EXT","Ext");
        streetTypeMap.put("EXTENSION","Ext");
        streetTypeMap.put("EXTN","Ext");
        streetTypeMap.put("EXTNSN","Ext");
        streetTypeMap.put("EXTENSIONS","Exts");
        streetTypeMap.put("EXTS","Exts");
        streetTypeMap.put("FALL","Fall");
        streetTypeMap.put("FALLS","Fls");
        streetTypeMap.put("FLS","Fls");
        streetTypeMap.put("FERRY","Fry");
        streetTypeMap.put("FRRY","Fry");
        streetTypeMap.put("FRY","Fry");
        streetTypeMap.put("FIELD","Fld");
        streetTypeMap.put("FLD","Fld");
        streetTypeMap.put("FIELDS","Flds");
        streetTypeMap.put("FLDS","Flds");
        streetTypeMap.put("FLAT","Flt");
        streetTypeMap.put("FLT","Flt");
        streetTypeMap.put("FLATS","Flts");
        streetTypeMap.put("FLTS","Flts");
        streetTypeMap.put("FORD","Frd");
        streetTypeMap.put("FRD","Frd");
        streetTypeMap.put("FORDS","Frds");
        streetTypeMap.put("FORG","Frg");
        streetTypeMap.put("FORGE","Frg");
        streetTypeMap.put("FRG","Frg");
        streetTypeMap.put("FORGES","Frgs");
        streetTypeMap.put("FORK","Frk");
        streetTypeMap.put("FRK","Frk");
        streetTypeMap.put("FORKS","Frks");
        streetTypeMap.put("FRKS","Frks");
        streetTypeMap.put("FORT","Ft");
        streetTypeMap.put("FRT","Ft");
        streetTypeMap.put("FT","Ft");
        streetTypeMap.put("GARDEN","Gdn");
        streetTypeMap.put("GARDN","Gdn");
        streetTypeMap.put("GDN","Gdn");
        streetTypeMap.put("GRDEN","Gdn");
        streetTypeMap.put("GRDN","Gdn");
        streetTypeMap.put("GARDENS","Gdns");
        streetTypeMap.put("GDNS","Gdns");
        streetTypeMap.put("GRDNS","Gdns");
        streetTypeMap.put("GATEWAY","Gtwy");
        streetTypeMap.put("GATEWY","Gtwy");
        streetTypeMap.put("GATWAY","Gtwy");
        streetTypeMap.put("GTWAY","Gtwy");
        streetTypeMap.put("GTWY","Gtwy");
        streetTypeMap.put("GLEN","Gln");
        streetTypeMap.put("GLN","Gln");
        streetTypeMap.put("GLENS","Glns");
        streetTypeMap.put("GREEN","Grn");
        streetTypeMap.put("GRN","Grn");
        streetTypeMap.put("GREENS","Grns");
        streetTypeMap.put("GROV","Grv");
        streetTypeMap.put("GROVE","Grv");
        streetTypeMap.put("GRV","Grv");
        streetTypeMap.put("GROVES","Grvs");
        streetTypeMap.put("HARB","Hbr");
        streetTypeMap.put("HARBOR","Hbr");
        streetTypeMap.put("HARBR","Hbr");
        streetTypeMap.put("HBR","Hbr");
        streetTypeMap.put("HRBOR","Hbr");
        streetTypeMap.put("HARBORS","Hbrs");
        streetTypeMap.put("HAVEN","Hvn");
        streetTypeMap.put("HAVN","Hvn");
        streetTypeMap.put("HVN","Hvn");
        streetTypeMap.put("HEIGHT","Hts");
        streetTypeMap.put("HEIGHTS","Hts");
        streetTypeMap.put("HGTS","Hts");
        streetTypeMap.put("HT","Hts");
        streetTypeMap.put("HTS","Hts");
        streetTypeMap.put("HILL","Hl");
        streetTypeMap.put("HL","Hl");
        streetTypeMap.put("HILLS","Hls");
        streetTypeMap.put("HLS","Hls");
        streetTypeMap.put("HLLW","Holw");
        streetTypeMap.put("HOLLOW","Holw");
        streetTypeMap.put("HOLLOWS","Holw");
        streetTypeMap.put("HOLW","Holw");
        streetTypeMap.put("HOLWS","Holw");
        streetTypeMap.put("INLET","Inlt");
        streetTypeMap.put("INLT","Inlt");
        streetTypeMap.put("IS","Is");
        streetTypeMap.put("ISLAND","Is");
        streetTypeMap.put("ISLND","Is");
        streetTypeMap.put("ISLANDS","Iss");
        streetTypeMap.put("ISLNDS","Iss");
        streetTypeMap.put("ISS","Iss");
        streetTypeMap.put("ISLE","Isle");
        streetTypeMap.put("ISLES","Isle");
        streetTypeMap.put("JCT","Jct");
        streetTypeMap.put("JCTION","Jct");
        streetTypeMap.put("JCTN","Jct");
        streetTypeMap.put("JUNCTION","Jct");
        streetTypeMap.put("JUNCTN","Jct");
        streetTypeMap.put("JUNCTON","Jct");
        streetTypeMap.put("JCTNS","Jcts");
        streetTypeMap.put("JCTS","Jcts");
        streetTypeMap.put("JUNCTIONS","Jcts");
        streetTypeMap.put("KEY","Ky");
        streetTypeMap.put("KY","Ky");
        streetTypeMap.put("KEYS","Kys");
        streetTypeMap.put("KYS","Kys");
        streetTypeMap.put("KNL","Knl");
        streetTypeMap.put("KNOL","Knl");
        streetTypeMap.put("KNOLL","Knl");
        streetTypeMap.put("KNLS","Knls");
        streetTypeMap.put("KNOLLS","Knls");
        streetTypeMap.put("LAKE","Lk");
        streetTypeMap.put("LK","Lk");
        streetTypeMap.put("LAKES","Lks");
        streetTypeMap.put("LKS","Lks");
        streetTypeMap.put("LAND","Land");
        streetTypeMap.put("LANDING","Lndg");
        streetTypeMap.put("LNDG","Lndg");
        streetTypeMap.put("LNDNG","Lndg");
        streetTypeMap.put("LA","Ln");
        streetTypeMap.put("LANE","Ln");
        streetTypeMap.put("LANES","Ln");
        streetTypeMap.put("LN","Ln");
        streetTypeMap.put("LGT","Lgt");
        streetTypeMap.put("LIGHT","Lgt");
        streetTypeMap.put("LIGHTS","Lgts");
        streetTypeMap.put("LF","Lf");
        streetTypeMap.put("LOAF","Lf");
        streetTypeMap.put("LCK","Lck");
        streetTypeMap.put("LOCK","Lck");
        streetTypeMap.put("LCKS","Lcks");
        streetTypeMap.put("LOCKS","Lcks");
        streetTypeMap.put("LDG","Ldg");
        streetTypeMap.put("LDGE","Ldg");
        streetTypeMap.put("LODG","Ldg");
        streetTypeMap.put("LODGE","Ldg");
        streetTypeMap.put("LOOP","Loop");
        streetTypeMap.put("LOOPS","Loop");
        streetTypeMap.put("MALL","Mall");
        streetTypeMap.put("MANOR","Mnr");
        streetTypeMap.put("MNR","Mnr");
        streetTypeMap.put("MANORS","Mnrs");
        streetTypeMap.put("MNRS","Mnrs");
        streetTypeMap.put("MDW","Mdw");
        streetTypeMap.put("MEADOW","Mdw");
        streetTypeMap.put("MDWS","Mdws");
        streetTypeMap.put("MEADOWS","Mdws");
        streetTypeMap.put("MEDOWS","Mdws");
        streetTypeMap.put("MEWS","Mews");
        streetTypeMap.put("MILL","Ml");
        streetTypeMap.put("ML","Ml");
        streetTypeMap.put("MILLS","Mls");
        streetTypeMap.put("MLS","Mls");
        streetTypeMap.put("MISSION","Msn");
        streetTypeMap.put("MISSN","Msn");
        streetTypeMap.put("MSN","Msn");
        streetTypeMap.put("MSSN","Msn");
        streetTypeMap.put("MOTORWAY","Mtwy");
        streetTypeMap.put("MNT","Mt");
        streetTypeMap.put("MOUNT","Mt");
        streetTypeMap.put("MT","Mt");
        streetTypeMap.put("MNTAIN","Mtn");
        streetTypeMap.put("MNTN","Mtn");
        streetTypeMap.put("MOUNTAIN","Mtn");
        streetTypeMap.put("MOUNTIN","Mtn");
        streetTypeMap.put("MTIN","Mtn");
        streetTypeMap.put("MTN","Mtn");
        streetTypeMap.put("MNTNS","Mtns");
        streetTypeMap.put("MOUNTAINS","Mtns");
        streetTypeMap.put("NCK","Nck");
        streetTypeMap.put("NECK","Nck");
        streetTypeMap.put("ORCH","Orch");
        streetTypeMap.put("ORCHARD","Orch");
        streetTypeMap.put("ORCHRD","Orch");
        streetTypeMap.put("OVAL","Oval");
        streetTypeMap.put("OVL","Oval");
        streetTypeMap.put("OVERPASS","Opas");
        streetTypeMap.put("PARK","Park");
        streetTypeMap.put("PK","Park");
        streetTypeMap.put("PRK","Park");
        streetTypeMap.put("PARKS","Park");
        streetTypeMap.put("PARKWAY","Pkwy");
        streetTypeMap.put("PARKWY","Pkwy");
        streetTypeMap.put("PKWAY","Pkwy");
        streetTypeMap.put("PKWY","Pkwy");
        streetTypeMap.put("PKY","Pkwy");
        streetTypeMap.put("PARKWAYS","Pkwy");
        streetTypeMap.put("PKWYS","Pkwy");
        streetTypeMap.put("PASS","Pass");
        streetTypeMap.put("PASSAGE","Psge");
        streetTypeMap.put("PATH","Path");
        streetTypeMap.put("PATHS","Path");
        streetTypeMap.put("PIKE","Pike");
        streetTypeMap.put("PIKES","Pike");
        streetTypeMap.put("PINE","Pne");
        streetTypeMap.put("PINES","Pnes");
        streetTypeMap.put("PNES","Pnes");
        streetTypeMap.put("PL","Pl");
        streetTypeMap.put("PLACE","Pl");
        streetTypeMap.put("PLAIN","Pln");
        streetTypeMap.put("PLN","Pln");
        streetTypeMap.put("PLAINES","Plns");
        streetTypeMap.put("PLAINS","Plns");
        streetTypeMap.put("PLNS","Plns");
        streetTypeMap.put("PLAZA","Plz");
        streetTypeMap.put("PLZ","Plz");
        streetTypeMap.put("PLZA","Plz");
        streetTypeMap.put("POINT","Pt");
        streetTypeMap.put("PT","Pt");
        streetTypeMap.put("POINTS","Pts");
        streetTypeMap.put("PTS","Pts");
        streetTypeMap.put("PORT","Prt");
        streetTypeMap.put("PRT","Prt");
        streetTypeMap.put("PORTS","Prts");
        streetTypeMap.put("PRTS","Prts");
        streetTypeMap.put("PR","Pr");
        streetTypeMap.put("PRAIRIE","Pr");
        streetTypeMap.put("PRARIE","Pr");
        streetTypeMap.put("PRR","Pr");
        streetTypeMap.put("RAD","Radl");
        streetTypeMap.put("RADIAL","Radl");
        streetTypeMap.put("RADIEL","Radl");
        streetTypeMap.put("RADL","Radl");
        streetTypeMap.put("RAMP","Ramp");
        streetTypeMap.put("RANCH","Rnch");
        streetTypeMap.put("RANCHES","Rnch");
        streetTypeMap.put("RNCH","Rnch");
        streetTypeMap.put("RNCHS","Rnch");
        streetTypeMap.put("RAPID","Rpd");
        streetTypeMap.put("RPD","Rpd");
        streetTypeMap.put("RAPIDS","Rpds");
        streetTypeMap.put("RPDS","Rpds");
        streetTypeMap.put("REST","Rst");
        streetTypeMap.put("RST","Rst");
        streetTypeMap.put("RDG","Rdg");
        streetTypeMap.put("RDGE","Rdg");
        streetTypeMap.put("RIDGE","Rdg");
        streetTypeMap.put("RDGS","Rdgs");
        streetTypeMap.put("RIDGES","Rdgs");
        streetTypeMap.put("RIV","Riv");
        streetTypeMap.put("RIVER","Riv");
        streetTypeMap.put("RIVR","Riv");
        streetTypeMap.put("RVR","Riv");
        streetTypeMap.put("RD","Rd");
        streetTypeMap.put("ROAD","Rd");
        streetTypeMap.put("RDS","Rds");
        streetTypeMap.put("ROADS","Rds");
        streetTypeMap.put("ROW","Row");
        streetTypeMap.put("RUE","Rue");
        streetTypeMap.put("RUN","Run");
        streetTypeMap.put("SERVICE DRIVE","Svc Dr");
        streetTypeMap.put("SERVICE DR","Svc Dr");
        streetTypeMap.put("SERVICE ROAD","Svc Rd");
        streetTypeMap.put("SERVICE RD","Svc Rd");
        streetTypeMap.put("SHL","Shl");
        streetTypeMap.put("SHOAL","Shl");
        streetTypeMap.put("SHLS","Shls");
        streetTypeMap.put("SHOALS","Shls");
        streetTypeMap.put("SHOAR","Shr");
        streetTypeMap.put("SHORE","Shr");
        streetTypeMap.put("SHR","Shr");
        streetTypeMap.put("SHOARS","Shrs");
        streetTypeMap.put("SHORES","Shrs");
        streetTypeMap.put("SHRS","Shrs");
        streetTypeMap.put("SKYWAY","Skwy");
        streetTypeMap.put("SPG","Spg");
        streetTypeMap.put("SPNG","Spg");
        streetTypeMap.put("SPRING","Spg");
        streetTypeMap.put("SPRNG","Spg");
        streetTypeMap.put("SPGS","Spgs");
        streetTypeMap.put("SPNGS","Spgs");
        streetTypeMap.put("SPRINGS","Spgs");
        streetTypeMap.put("SPRNGS","Spgs");
        streetTypeMap.put("SPUR","Spur");
        streetTypeMap.put("SPURS","Spur");
        streetTypeMap.put("SQ","Sq");
        streetTypeMap.put("SQR","Sq");
        streetTypeMap.put("SQRE","Sq");
        streetTypeMap.put("SQU","Sq");
        streetTypeMap.put("SQUARE","Sq");
        streetTypeMap.put("SQRS","Sqs");
        streetTypeMap.put("SQUARES","Sqs");
        streetTypeMap.put("STA","Sta");
        streetTypeMap.put("STATION","Sta");
        streetTypeMap.put("STATN","Sta");
        streetTypeMap.put("STN","Sta");
        streetTypeMap.put("STRA","Stra");
        streetTypeMap.put("STRAV","Stra");
        streetTypeMap.put("STRAVE","Stra");
        streetTypeMap.put("STRAVEN","Stra");
        streetTypeMap.put("STRAVENUE","Stra");
        streetTypeMap.put("STRAVN","Stra");
        streetTypeMap.put("STRVN","Stra");
        streetTypeMap.put("STRVNUE","Stra");
        streetTypeMap.put("STREAM","Strm");
        streetTypeMap.put("STREME","Strm");
        streetTypeMap.put("STRM","Strm");
        streetTypeMap.put("ST","St");
        streetTypeMap.put("STR","St");
        streetTypeMap.put("STREET","St");
        streetTypeMap.put("STRT","St");
        streetTypeMap.put("STREETS","Sts");
        streetTypeMap.put("SMT","Smt");
        streetTypeMap.put("SUMIT","Smt");
        streetTypeMap.put("SUMITT","Smt");
        streetTypeMap.put("SUMMIT","Smt");
        streetTypeMap.put("TER","Ter");
        streetTypeMap.put("TERR","Ter");
        streetTypeMap.put("TERRACE","Ter");
        streetTypeMap.put("THROUGHWAY","Trwy");
        streetTypeMap.put("TRACE","Trce");
        streetTypeMap.put("TRACES","Trce");
        streetTypeMap.put("TRCE","Trce");
        streetTypeMap.put("TRACK","Trak");
        streetTypeMap.put("TRACKS","Trak");
        streetTypeMap.put("TRAK","Trak");
        streetTypeMap.put("TRK","Trak");
        streetTypeMap.put("TRKS","Trak");
        streetTypeMap.put("TRAFFICWAY","Trfy");
        streetTypeMap.put("TRFY","Trfy");
        streetTypeMap.put("TR","Trl");
        streetTypeMap.put("TRAIL","Trl");
        streetTypeMap.put("TRAILS","Trl");
        streetTypeMap.put("TRL","Trl");
        streetTypeMap.put("TRLS","Trl");
        streetTypeMap.put("TUNEL","Tunl");
        streetTypeMap.put("TUNL","Tunl");
        streetTypeMap.put("TUNLS","Tunl");
        streetTypeMap.put("TUNNEL","Tunl");
        streetTypeMap.put("TUNNELS","Tunl");
        streetTypeMap.put("TUNNL","Tunl");
        streetTypeMap.put("UNDERPASS","Upas");
        streetTypeMap.put("UN","Un");
        streetTypeMap.put("UNION","Un");
        streetTypeMap.put("UNIONS","Uns");
        streetTypeMap.put("VALLEY","Vly");
        streetTypeMap.put("VALLY","Vly");
        streetTypeMap.put("VLLY","Vly");
        streetTypeMap.put("VLY","Vly");
        streetTypeMap.put("VALLEYS","Vlys");
        streetTypeMap.put("VLYS","Vlys");
        streetTypeMap.put("VDCT","Via");
        streetTypeMap.put("VIA","Via");
        streetTypeMap.put("VIADCT","Via");
        streetTypeMap.put("VIADUCT","Via");
        streetTypeMap.put("VIEW","Vw");
        streetTypeMap.put("VW","Vw");
        streetTypeMap.put("VIEWS","Vws");
        streetTypeMap.put("VWS","Vws");
        streetTypeMap.put("VILL","Vlg");
        streetTypeMap.put("VILLAG","Vlg");
        streetTypeMap.put("VILLAGE","Vlg");
        streetTypeMap.put("VILLG","Vlg");
        streetTypeMap.put("VILLIAGE","Vlg");
        streetTypeMap.put("VLG","Vlg");
        streetTypeMap.put("VILLAGES","Vlgs");
        streetTypeMap.put("VLGS","Vlgs");
        streetTypeMap.put("VILLE","Vl");
        streetTypeMap.put("VL","Vl");
        streetTypeMap.put("VIS","Vis");
        streetTypeMap.put("VIST","Vis");
        streetTypeMap.put("VISTA","Vis");
        streetTypeMap.put("VST","Vis");
        streetTypeMap.put("VSTA","Vis");
        streetTypeMap.put("WALK","Walk");
        streetTypeMap.put("WALKS","Walk");
        streetTypeMap.put("WALL","Wall");
        streetTypeMap.put("WAY","Way");
        streetTypeMap.put("WY","Way");
        streetTypeMap.put("WAYS","Ways");
        streetTypeMap.put("WELL","Wl");
        streetTypeMap.put("WELLS","Wls");
        streetTypeMap.put("WLS","Wls");
        streetTypeMap.put("BYU","Byu");
        streetTypeMap.put("BLFS","Blfs");
        streetTypeMap.put("BRKS","Brks");
        streetTypeMap.put("BG","Bg");
        streetTypeMap.put("BGS","Bgs");
        streetTypeMap.put("CTRS","Ctrs");
        streetTypeMap.put("CIRS","Cirs");
        streetTypeMap.put("CMN","Cmn");
        streetTypeMap.put("CTS","Cts");
        streetTypeMap.put("CVS","Cvs");
        streetTypeMap.put("CRST","Crst");
        streetTypeMap.put("XRD","Xrd");
        streetTypeMap.put("CURV","Curv");
        streetTypeMap.put("DRS","Drs");
        streetTypeMap.put("FRDS","Frds");
        streetTypeMap.put("FRGS","Frgs");
        streetTypeMap.put("GLNS","Glns");
        streetTypeMap.put("GRNS","Grns");
        streetTypeMap.put("GRVS","Grvs");
        streetTypeMap.put("HBRS","Hbrs");
        streetTypeMap.put("LGTS","Lgts");
        streetTypeMap.put("MTWY","Mtwy");
        streetTypeMap.put("MTNS","Mtns");
        streetTypeMap.put("OPAS","Opas");
        streetTypeMap.put("PSGE","Psge");
        streetTypeMap.put("PNE","Pne");
        streetTypeMap.put("RTE","Rte");
        streetTypeMap.put("SKWY","Skwy");
        streetTypeMap.put("SQS","Sqs");
        streetTypeMap.put("STS","Sts");
        streetTypeMap.put("TRWY","Trwy");
        streetTypeMap.put("UPAS","Upas");
        streetTypeMap.put("UNS","Uns");
        streetTypeMap.put("WL","Wl");
    }

    /* Highway mappings */
    static {
        highWayMap.put("CAM.", "Cam");
        highWayMap.put("CAMINO", "Cam");
        highWayMap.put("CO HWY", "Co Hwy");
        highWayMap.put("COUNTY HWY", "Co Hwy");
        highWayMap.put("COUNTY HIGHWAY", "Co Hwy");
        highWayMap.put("COUNTY HIGH WAY", "Co Hwy");
        highWayMap.put("COUNTY ROAD", "Co Rd");
        highWayMap.put("COUNTY RD", "Co Rd");
        highWayMap.put("CO RD", "Co Rd");
        highWayMap.put("CORD", "Co Rd");
        highWayMap.put("CO RTE", "Co Rte");
        highWayMap.put("COUNTY ROUTE", "Co Rte");
        highWayMap.put("CO ST AID HWY", "Co St Aid Hwy");
        highWayMap.put("EXP", "Expy");
        highWayMap.put("EXPR", "Expy");
        highWayMap.put("CAM", "Cam");
        highWayMap.put("EXPRESS", "Expy");
        highWayMap.put("EXPRESSWAY", "Expy");
        highWayMap.put("EXPW", "Expy");
        highWayMap.put("EXPY", "Expy");
        highWayMap.put("FARM RD", "Farm Rd");
        highWayMap.put("FIRE RD", "Fire Rd");
        highWayMap.put("FOREST RD", "Forest Rd");
        highWayMap.put("FOREST ROAD", "Forest Rd");
        highWayMap.put("FOREST RTE", "Forest Rte");
        highWayMap.put("FOREST ROUTE", "Forest Rte");
        highWayMap.put("FREEWAY", "Fwy");
        highWayMap.put("FREEWY", "Fwy");
        highWayMap.put("FRWAY", "Fwy");
        highWayMap.put("FRWY", "Fwy");
        highWayMap.put("FWY", "Fwy");
        highWayMap.put("HIGHWAY", "Hwy");
        highWayMap.put("HIGHWY", "Hwy");
        highWayMap.put("HIWAY", "Hwy");
        highWayMap.put("HIWY", "Hwy");
        highWayMap.put("HWAY", "Hwy");
        highWayMap.put("HWY", "Hwy");
        highWayMap.put("I", "I-");
        highWayMap.put("I-", "I-");
        highWayMap.put("INTERSTATE", "I-");
        highWayMap.put("INTERSTATE ROUTE", "I-");
        highWayMap.put("INTERSTATE RTE", "I-");
        highWayMap.put("INTERSTATE RTE.", "I-");
        highWayMap.put("INTERSTATE RT", "I-");
        highWayMap.put("ROUTE", "Rte");
        highWayMap.put("RT", "Rte");
        highWayMap.put("STATE HWY", "State Hwy");
        highWayMap.put("STATE HIGHWAY", "State Hwy");
        highWayMap.put("STATE HIGH WAY", "State Hwy");
        highWayMap.put("STATE RD", "State Rd");
        highWayMap.put("STATE ROAD", "State Rd");
        highWayMap.put("STATE ROUTE", "State Rte");
        highWayMap.put("STATE RTE", "State Rte");
        highWayMap.put("TPK", "Tpke");
        highWayMap.put("TPKE", "Tpke");
        highWayMap.put("TRNPK", "Tpke");
        highWayMap.put("TRPK", "Tpke");
        highWayMap.put("TURNPIKE", "Tpke");
        highWayMap.put("TURNPK", "Tpke");
        highWayMap.put("US HWY", "US Hwy");
        highWayMap.put("US HIGHWAY", "US Hwy");
        highWayMap.put("US HIGH WAY", "US Hwy");
        highWayMap.put("U.S.", "US Hwy");
        highWayMap.put("US RTE", "US Rte");
        highWayMap.put("US ROUTE", "US Rte");
        highWayMap.put("US RT", "US Rte");
        highWayMap.put("USFS HWY", "USFS Hwy");
        highWayMap.put("USFS HIGHWAY", "USFS Hwy");
        highWayMap.put("USFS HIGH WAY", "USFS Hwy");
        highWayMap.put("USFS RD", "USFS Rd");
        highWayMap.put("USFS ROAD", "USFS Rd");
    }

    /* Directional mappings */
    static {
        directionMap.put("WEST","W");
        directionMap.put("W","W");
        directionMap.put("SW","SW");
        directionMap.put("SOUTH-WEST","SW");
        directionMap.put("SOUTHWEST","SW");
        directionMap.put("SOUTH-EAST","SE");
        directionMap.put("SOUTHEAST","SE");
        directionMap.put("SOUTH_WEST","SW");
        directionMap.put("SOUTH_EAST","SE");
        directionMap.put("SOUTH","S");
        directionMap.put("SOUTH WEST","SW");
        directionMap.put("SOUTH EAST","SE");
        directionMap.put("SE","SE");
        directionMap.put("S","S");
        directionMap.put("NW","NW");
        directionMap.put("NORTH-WEST","NW");
        directionMap.put("NORTHWEST","NW");
        directionMap.put("NORTH-EAST","NE");
        directionMap.put("NORTHEAST","NE");
        directionMap.put("NORTH_WEST","NW");
        directionMap.put("NORTH_EAST","NE");
        directionMap.put("NORTH","N");
        directionMap.put("NORTH WEST","NW");
        directionMap.put("NORTH EAST","NE");
        directionMap.put("NE","NE");
        directionMap.put("N","N");
        directionMap.put("EAST","E");
        directionMap.put("E","E");
    }

    /** Street prefix map */
    static {
        streetPrefixMap.put("SAINT", "St");
        streetPrefixMap.put("FORT","Ft");
        streetPrefixMap.put("FRT","Ft");
        streetPrefixMap.put("FT","Ft");
    }
}
