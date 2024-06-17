"""
This script updates the SunSpec Models with new standards
"""
import json
import os


STD_NAME = 'IEEE 1547-2018'
MANDATORY_1547_POINTS = {  # ID and L are added by default
    1: ['Mn', 'Md', 'SN', 'Vr'],
    701: ['W', 'Var', 'Hz', 'St', 'ConnSt', 'Alrm', 'LLV', 'LNV', 'VL1L2', 'VL1', 'VL2L3', 'VL2', 'VL3L1', 'VL3'],
    702: ['WMaxRtg', 'WOvrExtRtg', 'WOvrExtRtgPF', 'WUndExtRtg', 'WUndExtRtgPF', 'VAMaxRtg', 'NorOpCatRtg',
          'AbnOpCatRtg', 'VarMaxInjRtg', 'VarMaxAbsRtg', 'WChaRteMaxRtg', 'VAChaRteMaxRtg', 'VNomRtg', 'VMaxRtg',
          'VMinRtg', 'CtrlModes', 'ReactSusceptRtg'],
    703: ['ES', 'ESVHi', 'ESVLo', 'ESHzHi', 'ESHzLo', 'ESDlyTms', 'ESRmpRem', 'V_SF', 'Hz_SF'],
    704: ['PFWInjEna', 'PF_SF', 'PFWInj.PF', 'PFWInj.Ext', 'VarSetEna', 'VarSetMod', 'VarSetPri', 'VarSetPct',
          'VarSetPct_SF', 'WMaxLimPctEna', 'WMaxLimPct', 'WMaxLimPct_SF'],
    705: ['Ena', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrv', 'V_SF', 'DeptRef_SF', 'RspTms_SF', 'Crv.ActPt',
          'Crv.DeptRef', 'Crv.Pri', 'Crv.VRef', 'Crv.VRefAutoEna', 'Crv.VRefAutoTms', 'Crv.RspTms', 'Crv.ReadOnly',
          'Crv.Pt.V', 'Crv.Pt.Var'],
    706: ['Ena', 'RspTms', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrv', 'V_SF', 'DeptRef_SF', 'RspTms_SF',
          'Crv.ActPt', 'Crv.DeptRef', 'Crv.RspTms', 'Crv.ReadOnly', 'Crv.Pt.V', 'Crv.Pt.W'],
    707: ['Ena', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrvSet', 'V_SF', 'Tms_SF', 'Crv.ReadOnly', 'Crv.MustTrip.ActPt',
          'Crv.MustTrip.Pt.V', 'Crv.MustTrip.Pt.Tms'],
    708: ['Ena', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrvSet', 'V_SF', 'Tms_SF', 'Crv.ReadOnly', 'Crv.MustTrip.ActPt',
          'Crv.MustTrip.Pt.V', 'Crv.MustTrip.Pt.Tms'],
    709: ['Ena', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrvSet', 'Hz_SF', 'Tms_SF', 'Crv.ReadOnly', 'Crv.MustTrip.ActPt',
          'Crv.MustTrip.Pt.Hz', 'Crv.MustTrip.Pt.Tms'],
    710: ['Ena', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrvSet', 'Hz_SF', 'Tms_SF', 'Crv.ReadOnly', 'Crv.MustTrip.ActPt',
          'Crv.MustTrip.Pt.Hz', 'Crv.MustTrip.Pt.Tms'],
    711: ['Ena', 'AdptCtlReq', 'AdptCtlRslt', 'NCtl', 'Db_SF', 'K_SF', 'RspTms_SF', 'Ctl.ReadOnly',
          'Ctl.DbOf', 'Ctl.DbUf', 'Ctl.KOf', 'Ctl.KUf', 'Ctl.RspTms'],
    712: ['Ena', 'AdptCrvReq', 'AdptCrvRslt', 'NPt', 'NCrv', 'W_SF', 'DeptRef_SF', 'Crv.ActPt', 'Crv.DeptRef',
          'Crv.Pri', 'Crv.ReadOnly', 'Crv.Pt.W', 'Crv.Pt.Var'],
    713: ['SoC'],
}


def group_parser(group, name, tabs=0, print_groups=False):
    """
    Parse the group and return a dictionary of the fields

    :param group: the group to parse
    :param name: the name of the group
    :param tabs: the number of tabs to print
    :param print_groups: whether to print the groups

    :return: a dictionary of the fields
    """

    if 'name' in group and print_groups:
        if name != '':
            print('\t' * tabs + '%s.%s (Group):' % (name, group['name']))
        else:
            print('\t' * tabs + '%s (Group):' % (group['name']))

    if 'points' in group:
        if name != '':
            pt_name = '%s.%s' % (name, group['name'])
            point_parser(group['points'], pt_name, tabs + 1)
        else:  # add ID and model name to the point name
            pt_name = '%s.%s' % (group['points'][0]['value'], group['name'])
            point_parser(group['points'], pt_name, tabs + 1)

    if 'groups' in group:
        for g in group['groups']:  # list of groups
            if name != '':
                gp_name = '%s.%s' % (name, group['name'])
                group_parser(g, gp_name, tabs + 1)
            else:  # add ID and model name to the group name
                gp_name = '%s.%s' % (group['points'][0]['value'], group['name'])
                group_parser(g, gp_name, tabs + 1)

    return group


def point_parser(points, name, tabs=0):
    """
    Parse the points and return a dictionary of the fields

    :param points: the points to parse
    :param name: the name string
    :param tabs: the number of tabs to print
    :return: a dictionary of the fields
    """

    for point in points:
        mandatory = False
        model = name.split('.')[0]  # get model number
        for pt in MANDATORY_1547_POINTS.get(int(model), []):
            if pt == '.'.join(name.split('.')[2:] + [point['name']]):
                mandatory = True
                break
        if point['name'] == 'ID' or point['name'] == 'L' or mandatory:
            if point.get('standards') is None:
                point['standards'] = [STD_NAME]
            elif STD_NAME not in point['standards']:
                point['standards'].append(STD_NAME)
            else:
                # check for duplicates and remove them
                point['standards'] = list(set(point['standards']))
            print('\t' * tabs + '%s.%s (+)' % (name, point['name']))
        else:
            if point.get('standards') is None:
                point['standards'] = []
            elif STD_NAME in point['standards']:
                point['standards'].remove(STD_NAME)
            print('\t' * tabs + '%s.%s (-)' % (name, point['name']))

    return points


def main():
    """
    Scan json files and add new fields to the dataset

    :return: None
    """
    json_path = os.path.join(os.path.dirname(os.path.realpath(__file__)), '..', 'json')
    for model, pts in MANDATORY_1547_POINTS.items():
        file = 'model_%s.json' % model
        with open(os.path.join(json_path, file), 'r') as f:
            data = json.load(f)

        print('Parsing ID %s' % data['id'])

        if 'points' in data:
            point_parser(data['points'], '')

        if 'group' in data:
            group_parser(data['group'], '')

        # write the new data back to the file
        with open(os.path.join(json_path, file), 'w') as f:
            json.dump(data, f, indent=4)


if __name__ == '__main__':
    main()

