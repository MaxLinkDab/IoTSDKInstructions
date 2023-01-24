package com.td.common_service.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.td.common.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Table(name = "device_info")
public class DeviceInfo extends BaseModel implements Serializable {
    /**
     * 设备总id
     */
    @Id
    private Integer id;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 设备编号
     */
    @Column(name = "device_no")
    private String deviceNo;

    /**
     * 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 设备cloudId
     */
    @Column(name = "cloud_id")
    private String cloudId;

    /**
     * sim卡卡号
     */
    @Column(name = "icc_id")
    private String iccId;

    @Column(name = "device_key")
    private String deviceKey;

    /**
     * 模块sn号
     */
    private String sn;

    /**
     * 设备状态(0禁用1启动2在线3离线)
     */
    @Column(name = "device_state")
    private Integer deviceState;

    /**
     * 事件id
     */
    private Long trace;

    /**
     * 设备仓位数(总仓位数)
     */
    @Column(name = "space_nu")
    private Integer spaceNu;

    /**
     * 设备从机数（总从机数）
     */
    @Column(name = "machine_nu")
    private Integer machineNu;

    /**
     * 设备uuID(设备上的)
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * 软件版本
     */
    @Column(name = "soft_version")
    private String softVersion;

    /**
     * 硬件版本
     */
    @Column(name = "hard_version")
    private String hardVersion;

    /**
     * 协议版本
     */
    @Column(name = "agreement_version")
    private String agreementVersion;

    /**
     * 扫码的url
     */
    private String url;

    /**
     * 设备型号
     */
    @Column(name = "device_model")
    private String deviceModel;

    /**
     * 信号
     */
    @Column(name = "device_signal")
    private String deviceSignal;

    /**
     * 网络机制（2G、3G、4G）
     */
    @Column(name = "network_type")
    private String networkType;

    /**
     * 网络运营商
     */
    @Column(name = "network_operator")
    private String networkOperator;

    /**
     * 设备IP
     */
    @Column(name = "device_ip")
    private String deviceIP;

    private static final long serialVersionUID = 1L;

    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Column(name = "sole_uid")
    private String soleUid; //销售代表
    @Column(name = "place_uid")
    private String placeUid;//场所管理员
    @Column(name = "agent_uid")
    private String agentUid;//场所管理员


    @Getter
    @Setter
    @Column(name = "place_uuid")
    private String placeUuid;//te

    @Transient
    private List<Powerbank> powerbankList;


    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    /**
     * 获取设备总id
     *
     * @return id - 设备总id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置设备总id
     *
     * @param id 设备总id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取创建时间
     *
     * @return created_time - 创建时间
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取设备编号
     *
     * @return device_no - 设备编号
     */
    public String getDeviceNo() {
        return deviceNo;
    }

    /**
     * 设置设备编号
     *
     * @param deviceNo 设备编号
     */
    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo == null ? null : deviceNo.trim();
    }

    /**
     * 获取设备名称
     *
     * @return device_name - 设备名称
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * 设置设备名称
     *
     * @param deviceName 设备名称
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName == null ? null : deviceName.trim();
    }

    /**
     * 获取设备cloudId
     *
     * @return cloud_id - 设备cloudId
     */
    public String getCloudId() {
        return cloudId;
    }

    /**
     * 设置设备cloudId
     *
     * @param cloudId 设备cloudId
     */
    public void setCloudId(String cloudId) {
        this.cloudId = cloudId == null ? null : cloudId.trim();
    }

    /**
     * 获取sim卡卡号
     *
     * @return icc_id - sim卡卡号
     */
    public String getIccId() {
        return iccId;
    }

    /**
     * 设置sim卡卡号
     *
     * @param iccId sim卡卡号
     */
    public void setIccId(String iccId) {
        this.iccId = iccId == null ? null : iccId.trim();
    }

    /**
     * @return device_key
     */
    public String getDeviceKey() {
        return deviceKey;
    }

    /**
     * @param deviceKey
     */
    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey == null ? null : deviceKey.trim();
    }

    /**
     * 获取模块sn号
     *
     * @return sn - 模块sn号
     */
    public String getSn() {
        return sn;
    }

    /**
     * 设置模块sn号
     *
     * @param sn 模块sn号
     */
    public void setSn(String sn) {
        this.sn = sn == null ? null : sn.trim();
    }

    /**
     * 获取设备状态(0禁用1启动2在线3离线)
     *
     * @return device_state - 设备状态(0禁用1启动2在线3离线)
     */
    public Integer getDeviceState() {
        return deviceState;
    }

    /**
     * 设置设备状态(0禁用1启动2在线3离线)
     *
     * @param deviceState 设备状态(0禁用1启动2在线3离线)
     */
    public void setDeviceState(Integer deviceState) {
        this.deviceState = deviceState;
    }

    /**
     * 获取事件id
     *
     * @return trace - 事件id
     */
    public Long getTrace() {
        return trace;
    }

    /**
     * 设置事件id
     *
     * @param trace 事件id
     */
    public void setTrace(Long trace) {
        this.trace = trace;
    }

    /**
     * 获取设备仓位数(总仓位数)
     *
     * @return space_nu - 设备仓位数(总仓位数)
     */
    public Integer getSpaceNu() {
        return spaceNu;
    }

    /**
     * 设置设备仓位数(总仓位数)
     *
     * @param spaceNu 设备仓位数(总仓位数)
     */
    public void setSpaceNu(Integer spaceNu) {
        this.spaceNu = spaceNu;
    }

    /**
     * 获取设备从机数（总从机数）
     *
     * @return machine_nu - 设备从机数（总从机数）
     */
    public Integer getMachineNu() {
        return machineNu;
    }

    /**
     * 设置设备从机数（总从机数）
     *
     * @param machineNu 设备从机数（总从机数）
     */
    public void setMachineNu(Integer machineNu) {
        this.machineNu = machineNu;
    }

    /**
     * 获取设备uuID(设备上的)
     *
     * @return device_uuid - 设备uuID(设备上的)
     */
    public String getDeviceUuid() {
        return deviceUuid;
    }

    /**
     * 设置设备uuID(设备上的)
     *
     * @param deviceUuid 设备uuID(设备上的)
     */
    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid == null ? null : deviceUuid.trim();
    }

    /**
     * 获取软件版本
     *
     * @return soft_version - 软件版本
     */
    public String getSoftVersion() {
        return softVersion;
    }

    /**
     * 设置软件版本
     *
     * @param softVersion 软件版本
     */
    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion == null ? null : softVersion.trim();
    }

    /**
     * 获取硬件版本
     *
     * @return hard_version - 硬件版本
     */
    public String getHardVersion() {
        return hardVersion;
    }

    /**
     * 设置硬件版本
     *
     * @param hardVersion 硬件版本
     */
    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion == null ? null : hardVersion.trim();
    }

    /**
     * 获取协议版本
     *
     * @return agreement_version - 协议版本
     */
    public String getAgreementVersion() {
        return agreementVersion;
    }

    /**
     * 设置协议版本
     *
     * @param agreementVersion 协议版本
     */
    public void setAgreementVersion(String agreementVersion) {
        this.agreementVersion = agreementVersion == null ? null : agreementVersion.trim();
    }

    /**
     * 获取结果回调的url
     *
     * @return url - 扫码的url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置结果回调的url
     *
     * @param url 扫码的url
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * 获取设备型号
     *
     * @return device_model - 设备型号
     */
    public String getDeviceModel() {
        return deviceModel;
    }

    /**
     * 设置设备型号
     *
     * @param deviceModel 设备型号
     */
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel == null ? null : deviceModel.trim();
    }

    /**
     * 获取信号
     *
     * @return device_signal - 信号
     */
    public String getDeviceSignal() {
        return deviceSignal;
    }

    /**
     * 设置信号
     *
     * @param deviceSignal 信号
     */
    public void setDeviceSignal(String deviceSignal) {
        this.deviceSignal = deviceSignal == null ? null : deviceSignal.trim();
    }

    /**
     * 获取网络机制（2G、3G、4G）
     *
     * @return network_type - 网络机制（2G、3G、4G）
     */
    public String getNetworkType() {
        return networkType;
    }

    /**
     * 设置网络机制（2G、3G、4G）
     *
     * @param networkType 网络机制（2G、3G、4G）
     */
    public void setNetworkType(String networkType) {
        this.networkType = networkType == null ? null : networkType.trim();
    }

    /**
     * 获取网络运营商
     *
     * @return network_operator - 网络运营商
     */
    public String getNetworkOperator() {
        return networkOperator;
    }

    /**
     * 设置网络运营商
     *
     * @param networkOperator 网络运营商
     */
    public void setNetworkOperator(String networkOperator) {
        this.networkOperator = networkOperator == null ? null : networkOperator.trim();
    }

    public String getSoleUid() {
        return soleUid;
    }

    public void setSoleUid(String soleUid) {
        this.soleUid = soleUid;
    }

    public String getPlaceUid() {
        return placeUid;
    }

    public void setPlaceUid(String placeUid) {
        this.placeUid = placeUid;
    }

    public String getAgentUid() {
        return agentUid;
    }

    public void setAgentUid(String agentUid) {
        this.agentUid = agentUid;
    }

    public List<Powerbank> getPowerbankList() {
        return powerbankList;
    }

    public void setPowerbankList(List<Powerbank> powerbankList) {
        this.powerbankList = powerbankList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", deviceNo=").append(deviceNo);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", cloudId=").append(cloudId);
        sb.append(", iccId=").append(iccId);
        sb.append(", deviceKey=").append(deviceKey);
        sb.append(", sn=").append(sn);
        sb.append(", deviceState=").append(deviceState);
        sb.append(", trace=").append(trace);
        sb.append(", spaceNu=").append(spaceNu);
        sb.append(", machineNu=").append(machineNu);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", softVersion=").append(softVersion);
        sb.append(", hardVersion=").append(hardVersion);
        sb.append(", agreementVersion=").append(agreementVersion);
        sb.append(", url=").append(url);
        sb.append(", deviceModel=").append(deviceModel);
        sb.append(", deviceSignal=").append(deviceSignal);
        sb.append(", networkType=").append(networkType);
        sb.append(", networkOperator=").append(networkOperator);
        sb.append("]");
        return sb.toString();
    }
}
