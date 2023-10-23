import os
import time
import random
import subprocess
import psutil
from concurrent.futures import ThreadPoolExecutor, as_completed

# Function to generate a random number between min and max (inclusive)
def get_random_duration_minutes():
    return random.randint(60, 300)

def start_new_instance():
    process = subprocess.Popen(["java", "-jar", "OSRSBot.jar", "--bot-runelite", "-dev", "-stacktrace"])
    pid = process.pid
    start_times[pid] = time.time()
    stop_times[pid] = get_random_duration_minutes()

# Number of applications to run simultaneously
MAX_APPS = 5

# Dictionary to store the start times for each PID
start_times = {}
stop_times = {}

while True:
    try:
        # Get the current number of running instances
        running_apps = len([pid for pid in start_times.keys() if os.path.exists(f"/proc/{pid}")])

        # Calculate how many more instances to start
        instances_to_start = MAX_APPS - running_apps

        if instances_to_start > 0:
        # Start new instances using a ThreadPoolExecutor
            with ThreadPoolExecutor(max_workers=instances_to_start) as executor:
                future_tasks = []
                for _ in range(instances_to_start):
                    # Submit tasks and store the Future objects in a list
                    future_tasks.append(executor.submit(start_new_instance))
                    time.sleep(5)


                # Wait for any completed instance and start new ones in parallel
                for completed_task in as_completed(future_tasks):
                    completed_task.result()  # This ensures the start_new_instance function is executed
    


        # Get the current time
        current_time = time.time()

        # Check if any running instances have exceeded their random duration
        pids_to_remove = []  # Keep track of PIDs to remove from the dictionary
        for pid, start_time in list(start_times.items()):
            duration = (current_time - start_time) / 60
            process = psutil.Process(pid)
            #todo: sometimes the pid doesnt exist anymore, remove it, and do process = in try catch?
            print(process.status())
            if process.status() == psutil.STATUS_ZOMBIE:
                # The process has terminated (manually closed), remove it from the dictionary
                print(f"PID: {pid} was manually closed.")
                pids_to_remove.append(pid)
            elif duration >= stop_times[pid]:
                print(f"KILLING PID: {pid}, current duration: {duration:.2f} minutes, and time to stop: {stop_times[pid]} minutes")
                try:
                    os.kill(pid, 9)  # SIGKILL
                except ProcessLookupError:
                    pass
                pids_to_remove.append(pid)  # Mark this PID for removal
            else:
                print(f"PID: {pid}, current duration: {duration:.2f} minutes, and time to stop: {stop_times[pid]} minutes")




        # Remove stopped processes from the dictionaries
        for pid in pids_to_remove:
            del start_times[pid]
            del stop_times[pid]

        # Sleep for a short time before checking again
        time.sleep(5 * 60)
    except Exception as e:
        print(e)