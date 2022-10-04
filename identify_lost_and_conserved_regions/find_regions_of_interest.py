### Natalie Kalina ###
### v1.1 ###

import time, os, sys
from argparse import ArgumentParser

input_parameters = ArgumentParser()
input_parameters.add_argument("--output_dir", "--output", "--out", "--o", dest="output_dir")
input_parameters.add_argument("--cov_var", dest="cov_var")
input_parameters.add_argument("--mode", dest="mode")
input_parameters.add_argument("--assembly_type", dest="assembly")
input_parameters.add_argument("--max_cov", dest="max_cov_percentage", type=float)
input_parameters.add_argument("--var", dest="var_percentage", type=float)
input_parameters.add_argument("--window_size", dest="window_size", type=int)
input_parameters.add_argument("--shift", dest="shift", type=int)
input_parameters.add_argument("--min_cov", dest="min_cov_percentage", type=float)

__usage__ =	"""
python3 find_regions_of_interest.py\n
REQUIRED:
--output_dir <FULL_PATH_TO_DIRECTORY>\n
--cov_var <FULL_PATH_TO_FILE TO COVERAGE-VARIANT FILE (TSV OF FORMAT <contig> <position> <coverage> <variant (0 or 1)>)>\n
--mode <EITHER "conserved" OR "lost">\n
--contigs <FULL PATH TO FILE CONTAINGING LIST OF CONTIGS>\n
--max_cov <MAXIMAL COVERAGE THRESHOLD RELATIVE TO MEAN>\n
--var <VAR THRESHOLD RELATIVE TO MEAN>\n
--window_size <WINDOW SIZE IN BP>\n
--shift <SHIFT SIZE IN BP>\n
--assembly_type <EITHER "short" OR "long">\n
OPTIONAL:
--min_cov <MINIMAL COVERAGE THRESHOLD. ONLY REQUIRED IF --mode SET TO "conserved">\n
			"""
	
""" find conserved/lost regions based on set coverage and variant thresholds via a window-based approach"""
def find_regions(args, contig, cov_var_dic, cov_t_max, var_t, cov_t_min, window_size, shift):
		
	# # --- begin search ---
	cov = [float(entry[0]) for entry in cov_var_dic[contig]]
	var = [float(entry[1]) for entry in cov_var_dic[contig]]


	windows = [] # (start-pos, middle-pos, end-pos, cov, var); stored for plotting
	passing_windows = [] # (start-pos, middle-pos, end-pos, cov, var)
	number_of_found_windows = 0
	windows_total_coverage = 0
	windows_number_of_variants = 0
	
	# calculate first window
	start_pos = 0
	end_pos = window_size
	for i in range(start_pos, end_pos):
		
		windows_total_coverage += int(cov[i])
		windows_number_of_variants += int(var[i])

	average_coverage = windows_total_coverage/window_size
	average_variants = windows_number_of_variants/window_size
	windows.append((1,(window_size/2), window_size, float(average_coverage), float(average_variants)))	
	
	# check first window
	if args.mode == "lost":
		if average_coverage <= cov_t_max and average_variants >= var_t:
			number_of_found_windows += 1			
			passing_windows.append((1,int(window_size/2), window_size, float(average_coverage), float(average_variants)))
	else:
		if average_coverage <= cov_t_max and average_coverage >= cov_t_min and average_variants <= var_t:
			number_of_found_windows += 1			
			passing_windows.append((1,int(window_size/2), window_size, float(average_coverage), float(average_variants)))	
		
	# sliding to the right for all further windows
	for i in range(shift, len(cov) - window_size, shift):
		for j in range(shift):
			windows_total_coverage -= int(cov[start_pos+j])
			windows_number_of_variants -= int(var[start_pos+j])
			windows_total_coverage += int(cov[end_pos+j])
			windows_number_of_variants += int(var[end_pos+j])
			
		average_coverage = windows_total_coverage/window_size
		average_variants = windows_number_of_variants/window_size
	
		windows.append((i + 1,i + (window_size/2), i + window_size, float(average_coverage), float(average_variants)))
		if args.mode == "lost":
			if average_coverage <= cov_t_max and average_variants >= var_t:
				number_of_found_windows += 1
				passing_windows.append((i + 1, i + (window_size/2), i + window_size, float(average_coverage), float(average_variants)))
		else:
			if average_coverage <= cov_t_max and average_coverage >= cov_t_min and average_variants <= var_t:
				number_of_found_windows += 1
				passing_windows.append((i + 1, i + (window_size/2), i + window_size, float(average_coverage), float(average_variants)))
						
		start_pos += shift
		end_pos += shift
	
	with open(output_path_passing, "a") as output_passing, open(output_windows, "a") as output_all:
		for window in windows:
			output_all.write(contig + "\t" + str(window[0]) + "\t" + str(window[1]) + "\t" + str(window[2]) + "\t" +  str(window[3]) + "\t" + str(window[4]) + "\n")
		
		for passing_window in passing_windows:
			output_passing.write(contig + "\t" + str(passing_window[0]) + "\t" + str(passing_window[1]) + "\t" + str(passing_window[2]) +  "\t" + str(passing_window[3]) + "\t" + str(passing_window[4])  + "\n")
		
	# get extended regions and write them to file
	if len(passing_windows) > 0:
		extended_regions = extend_regions(passing_windows)
	else:
		extended_regions = []
		
	with open(output_path_regions, "a") as regions:
		for i in range(len(extended_regions)):
			regions.write(str(contig) + "\t" + str(extended_regions[i][0]) + "\t" + str(extended_regions[i][2]) + "\n")
	regions.close()

	
	covered_bases = get_covered_bases(extended_regions)
	percentage_covered = round(((covered_bases/len(cov)) * 100),3)

	# write stats to stats-file
	stats = open(stats_file, "a")
	stats.write(contig + "\t" + str(window_size) + "\t" + str(shift) + "\t" +  str(args.max_cov_percentage) + "\t" + str(round(cov_t_max, 4)) + "\t"  + str(args.var_percentage) + "\t" + str(round(var_t, 4)) + "\t" + str(covered_bases) + "\t" + str(round(percentage_covered,3)) + "\t" + str(number_of_found_windows) +  "\t" + str(len(extended_regions)) + "\n")
	stats.close()
	


# find extended regions that fulfill the threshold. return their start and end position
def extend_regions(windows):
	regions = []
	regions.append(windows[0])
	for i in range(1, len(windows)):
		element = regions.pop()
		if overlap(element, windows[i]):
			next_element = (element[0], element[0] + round((max(element[2], windows[i][2]) - element[0])/2), max(element[2], windows[i][2]))
			regions.append(next_element)
		else:
			regions.append(element)
			regions.append(windows[i])
	return regions
	
# helper function. Checks if two regions overlap
def overlap(first, second):
	if second[0] > first[0] and second[0] < first[2]:
		return True
	else:
		return False
		
		
# calculates the number of based covered by identified regions
def get_covered_bases(extended_regions):
	covered_bases = 0
	if len(extended_regions) == 0:
		return 0
	for i in range(len(extended_regions)):
			covered_bases += (extended_regions[i][2] - extended_regions[i][0] + 1 )
	return covered_bases
	

def main(args):
	
	# set params:
	if args.window_size is None:
		window_size = 2500
		print("'--window_size' was not set. Setting to default (2500)")
	else:
		window_size = args.window_size
	if args.shift is None:
		shift = 150
		print("'--shift' was not set. Setting to default (150)")
	else:
		shift = args.shift
	if args.max_cov_percentage is None:
		if args.mode == "lost":
			max_cov = 0.1
		else:
			max_cov = 3
		print("'--max_cov' was not set. Setting to default (0.1 for mode 'lost', 3.0 for mode 'conserved')")
	else:
		max_cov = args.max_cov_percentage
	
	if args.mode == "conserved"  and args.min_cov_percentage is None:
		print("'--mode' was set to 'conserved' but '--min_cov' was no set. Setting to default (1.0)")
		min_cov = 1.0
	elif args.mode == "conserved" and not args.min_cov_percentage is None:
		min_cov = args.min_cov_percentage
	
	if args.var_percentage is None:
		print("'--var' was not set. Setting to default (0.4)")
		var = 0.4
	else:
		var = args.var_percentage
		
	
	# store coverage and variant information contig-wise in a dictionary
	
	dic = {}
	count = 0
	cov_sum = 0
	var_sum = 0
	start = time.time()
	print("reading in coverage and variant information...")
	with open(args.cov_var, "r") as cv_file:
		for line in cv_file:
			line = line.split()
			if line[0] not in dic:
				dic[line[0]] = []
			dic[line[0]].append((line[2],line[3]))
			cov_sum += float(line[2])
			var_sum += float(line[3])
			count+= 1
			if count % 50000000 == 0:
				print(str(count) + " positions processed")
	
	
	contig_names = dic.keys()
	
	# in case of long read-assemblies: use means for every contig stored in the dictionary
	if args.assembly == "long":
		for contig in contig_names:
			
			# if a contig provided in the --contig argument is not present in --cov_var file, skip
			if contig not in dic:
				print(contig  + " is not present in provided --cov_var file and is skipped.")
				continue
			cov = [float(entry[0]) for entry in dic[contig]]
			var = [float(entry[1]) for entry in dic[contig]]
			
			if len(cov) <= args.window_size or len(var) <= args.window_size:
				continue
			
			# calculate thresholds
			cov_t_max = round(sum(cov)/len(cov) * args.max_cov_percentage)
			var_t = sum(var)/len(var) * args.var_percentage
			
			print(cov_t_max, var_t)
			if args.mode == "conserved":
				cov_t_min = round(sum(cov)/len(cov) * args.min_cov_percentage)
			else:
				cov_t_min = None
			find_regions(args, contig.strip(), dic, cov_t_max, var_t, cov_t_min, args.window_size, shift)
			
	# for short read assemblies: use the overall means
	else:
		cov_t_max = cov_sum/count * args.max_cov_percentage
		var_t = var_sum/count * args.var_percentage
		if args.mode == "conserved":
				cov_t_min = sum(cov)/len(cov) * args.min_cov_percentage
		else:
			cov_t_min = None
		for contig in contig_names:	
			find_regions(args, contig.strip(), dic, cov_t_max, var_t, cov_t_min, window_size, shift)
	
	
if "--help" in sys.argv or "-h" in sys.argv:
    print(__usage__)
    sys.exit(1)

args = input_parameters.parse_args()
if args.output_dir is None:
	print("\n'--output_dir' was not set")
	print(__usage__)
	sys.exit(1)
elif args.cov_var is None:
	print("\n'--cov_var' was not set")
	print(__usage__)
	sys.exit(1)	
elif args.mode != "lost" and args.mode != "conserved":
	print("invalid mode selected. Select 'lost' or 'conserved'")
	sys.exit(1)
elif args.assembly != "long" and args.assembly != "short":
	print("invalid assembly type selected. Select 'long' or 'short'")
	sys.exit(1)
else:
	output_windows = args.output_dir + "windows.txt"
	output_path_passing =  args.output_dir + "passing_windows.txt"
	output_path_regions = args.output_dir + "extended_regions.txt"
	stats_file = args.output_dir + "stats.txt"
	
	if os.path.exists(output_windows):
		os.remove(output_windows)
	if os.path.exists(output_path_passing):
		os.remove(output_path_passing)
	if os.path.exists(output_path_regions):
		os.remove(output_path_regions)
	if os.path.exists(stats_file):
		os.remove(stats_file)
	with open(stats_file, "w") as stats:
		stats.write("contig\twindow_size\tshift\tmax_cov%\tcov_t_max\tvar%\tvar_t\tcovered_bases\t% covered\tno.windows\tno.regions\n")
	if __name__ == "__main__":
		main(args)
