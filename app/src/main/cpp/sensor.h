#include <stdint.h>

#define COREAIOT_MANUFACTURER_ID 0x000d
#define COREAIOT_MANUFACTURER_SPECIFIC_DATA_LENGTH 27
#define COREAIOT_NUMBER_OF_SERVICE_UUIDS 13

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 生成 manufacturer data
 * @param id
 * MAC 地址的后两个字节
 *   MAC 前 4 个字节固定为 43544d41
 *   值域：[0, 0xffff]
 * @param alarm
 * 报警状态 0 为正常，1 为报警
 * @param battery
 * 电池电量
 *   值域：[0, 10]
 *   实际电量为 值 * 10 * 100%
 *   例：值为 5 表示电池电量为 50%
 * @param advertise_mode
 * 广播频率
 *   0 - 低耗电
 *   1 - 平衡
 *   2 - 低延迟
 * @param tx_power_level
 * 广播功率
 *   0 - 极低
 *   1 - 低
 *   2 - 中
 *   3 - 高
 * @param channel
 * 广播信道
 *   0 - 37
 *   1 - 38
 *   2 - 39
 * @return uint8_t 数组指针，长度为 COREAIOT_MANUFACTURER_SPECIFIC_DATA_LENGTH
 */
uint8_t *coreaiot_generate_manufacturer_specific_data(
        uint16_t id, uint8_t alarm, uint8_t battery, uint8_t advertise_mode,
        uint8_t tx_power_level, uint8_t channel);

/**
 * 生成 service uuid 数组
 * @param id
 * MAC 地址的后两个字节
 *   MAC 前 4 个字节固定为 43544d41
 *   值域：[0, 0xffff]
 * @param alarm
 * 报警状态 0 为正常，1 为报警
 * @param battery
 * 电池电量
 *   值域：[0, 10]
 *   实际电量为 值 * 10 * 100%
 *   例：值为 5 表示电池电量为 50%
 * @return uint16_t 数组指针，长度为 COREAIOT_NUMBER_OF_SERVICE_UUIDS
 */
uint16_t *coreaiot_generate_service_uuids(uint16_t id, uint8_t alarm,
                                          uint8_t battery);

#ifdef __cplusplus
}
#endif