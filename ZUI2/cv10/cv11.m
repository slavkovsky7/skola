%% Data
clear all
close all

% rewards
world.rewardStep = -0.03;
world.rewardGoodGoal = 1;
world.rewardBadGoal = -1;

world.blank = [2 2]; % unusable grid state
world.goal1 = [1 4]; % "good" exit state
world.goal2 = [2 4]; % "bad exit state

world.R = ones(3, 4) * world.rewardStep;
world.R(world.blank(1), world.blank(2)) = 0;
world.R(world.goal1(1), world.goal1(2)) = world.rewardGoodGoal;
world.R(world.goal2(1), world.goal2(2)) = world.rewardBadGoal;

world.probs = [0.8 0.1 0 0.1]; %prob[on righ back left];

world.actions = [ -1, 0 % up
            0, 1 % right
            1, 0 % down
            0, -1]; % left
            
seq = [1,1,2,2,2,4,2];
start = [3,1]; % bottom left

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%55

function ret = dfs( actions, currentState, world )
	if size(actions,1) == 0
		ret = currentState;
	else
		ret = [];
		for i= 1 : 4
			next = next_state(world,currentState,i);
			if !prod(next == currentState)
				if size(actions,2) > 1
					newActions = actions(2:end); 
				else
					newActions = [];
				end	
				dfsRet = dfs( newActions, next, world );
				ret = [ ret ; dfsRet ];
			end
		end
	end
end

ret = dfs([1,1], start, world)
