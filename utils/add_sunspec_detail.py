"""
This script updates the SunSpec Models with new Detailed Descriptions
"""
import json
import os


# COMMENT = "Internal curve conformance checks should be conducted when AdptCrvReq is set to 1, not on point writes."
# COMMENT_POINTS = {
#     705: ['Crv.Pt.V', 'Crv.Pt.Var'],
#     706: ['Crv.Pt.V', 'Crv.Pt.W'],
#     707: ['Crv.MustTrip.Pt.V', 'Crv.MustTrip.Pt.Tms'],
#     708: ['Crv.MustTrip.Pt.V', 'Crv.MustTrip.Pt.Tms'],
#     709: ['Crv.MustTrip.Pt.Hz', 'Crv.MustTrip.Pt.Tms'],
#     710: ['Crv.MustTrip.Pt.Hz', 'Crv.MustTrip.Pt.Tms'],
#     712: ['Crv.Pt.W', 'Crv.Pt.Var'],
# }

COMMENT = ("Voltages are LN for single phase DER (e.g. 120 V nominal), LL for split phase DER "
           "(e.g. 240 V nominal), and LL for three phase DER (e.g., 480 V nominal).")
COMMENT_POINTS = {
    702: ['VNomRtg', 'VMaxRtg', 'VMinRtg', 'VNom', 'VMax', 'VMin'],
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
        mandatory = False  # flag to update point data
        model = name.split('.')[0]  # get model number
        for pt in COMMENT_POINTS.get(int(model), []):
            if pt == '.'.join(name.split('.')[2:] + [point['name']]):
                mandatory = True
                break
        if mandatory:
            point['detail'] = COMMENT
            print('\t' * tabs + '%s.%s (+)' % (name, point['name']))
        else:
            print('\t' * tabs + '%s.%s (-)' % (name, point['name']))

    return points


def main():
    """
    Scan json files and add new fields to the dataset

    :return: None
    """
    json_path = os.path.join(os.path.dirname(os.path.realpath(__file__)), '..', 'json')
    for model, pts in COMMENT_POINTS.items():
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

