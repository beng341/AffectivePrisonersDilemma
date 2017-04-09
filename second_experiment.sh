#! /bin/bash
cd ~/NetBeansProjects/AffectivePrisonersDilemma/build/classes


# echo "${EPA_SEEDS[*]}"

# SEED_COUNT=1
SEEDS=(2481 6221 6744 7289 8218 9919 10963 11006 12724 12777 18583 18758 19441 19595 19783 21568 22965 25303 26876 31784)
# until [[ $SEED_COUNT -gt 20 ]]; do
# 	SEEDS+=($RANDOM)
# 	let SEED_COUNT=SEED_COUNT+1
# done
echo "${SEEDS[*]}"

# for (( i = 0; i < 9; i++ )); do 	# EPA loop first
	for (( j = 0; j < 20; j++ )); do # standard seed loop second
		echo "${SEEDS[j]}"
		java affectiveprisonersdilemma.Population -seed ${SEEDS[${j}]} -EPA 0 -output out.csv -until 5000
	done
# done