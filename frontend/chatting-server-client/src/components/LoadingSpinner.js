import { CircularProgress, Box } from '@mui/material';

function LoadingSpinner() {
  return (
    <Box 
      sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center',
        height: '100%',
        minHeight: '200px'
      }}
    >
      <CircularProgress />
    </Box>
  );
}

export default LoadingSpinner; 