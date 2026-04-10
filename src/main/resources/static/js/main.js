// Main JavaScript for Movie Booking System

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Image lazy loading
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });

        document.querySelectorAll('img[data-src]').forEach(img => {
            imageObserver.observe(img);
        });
    }

    // Search functionality
    const searchInput = document.querySelector('input[name="search"]');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                // Implement live search if needed
                console.log('Searching for:', this.value);
            }, 300);
        });
    }

    // Rating system
    initializeRatingSystem();
    
    // Admin dashboard charts
    initializeAdminCharts();
    
    // Movie management functionality
    initializeMovieManagement();
});

// Rating System Functions
function initializeRatingSystem() {
    const ratingInputs = document.querySelectorAll('.rating-input');
    ratingInputs.forEach(input => {
        input.addEventListener('change', function() {
            updateRatingDisplay(this);
        });
    });
}

function updateRatingDisplay(input) {
    const rating = parseInt(input.value);
    const container = input.closest('.rating-container');
    const stars = container.querySelectorAll('.rating-star');
    
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}

// Admin Dashboard Functions
function initializeAdminCharts() {
    // Genre Distribution Chart
    const genreCtx = document.getElementById('genreChart');
    if (genreCtx) {
        new Chart(genreCtx, {
            type: 'doughnut',
            data: {
                labels: ['Action', 'Comedy', 'Drama', 'Horror', 'Romance', 'Sci-Fi', 'Thriller'],
                datasets: [{
                    data: [12, 19, 3, 5, 2, 8, 6],
                    backgroundColor: [
                        '#FF6384',
                        '#36A2EB',
                        '#FFCE56',
                        '#4BC0C0',
                        '#9966FF',
                        '#FF9F40',
                        '#FF6384'
                    ],
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true
                        }
                    }
                }
            }
        });
    }

    // Booking Trends Chart
    const bookingCtx = document.getElementById('bookingChart');
    if (bookingCtx) {
        new Chart(bookingCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug'],
                datasets: [{
                    label: 'Bookings',
                    data: [12, 19, 3, 5, 2, 3, 8, 15],
                    borderColor: '#36A2EB',
                    backgroundColor: 'rgba(54, 162, 235, 0.1)',
                    tension: 0.4,
                    fill: true
                }, {
                    label: 'Revenue',
                    data: [1200, 1900, 300, 500, 200, 300, 800, 1500],
                    borderColor: '#28a745',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.4,
                    fill: true,
                    yAxisID: 'y1'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        type: 'linear',
                        display: true,
                        position: 'left',
                        title: {
                            display: true,
                            text: 'Bookings'
                        }
                    },
                    y1: {
                        type: 'linear',
                        display: true,
                        position: 'right',
                        title: {
                            display: true,
                            text: 'Revenue ($)'
                        },
                        grid: {
                            drawOnChartArea: false,
                        },
                    }
                },
                plugins: {
                    legend: {
                        position: 'top',
                    }
                }
            }
        });
    }
}

// Movie Management Functions
function initializeMovieManagement() {
    // Add movie form validation
    const addMovieForm = document.querySelector('#addMovieModal form');
    if (addMovieForm) {
        addMovieForm.addEventListener('submit', function(e) {
            e.preventDefault();
            validateAndSubmitMovie(this);
        });
    }

    // Edit movie functionality
    window.editMovie = function(movieId) {
        // Fetch movie data and populate edit form
        fetch(`/admin/movies/${movieId}`)
            .then(response => response.json())
            .then(movie => {
                populateEditForm(movie);
                const editModal = new bootstrap.Modal(document.getElementById('editMovieModal'));
                editModal.show();
            })
            .catch(error => {
                console.error('Error fetching movie:', error);
                showNotification('Error loading movie data', 'error');
            });
    };

    // Delete movie functionality
    window.deleteMovie = function(movieId) {
        if (confirm('Are you sure you want to delete this movie? This action cannot be undone.')) {
            const form = document.createElement('form');
            form.method = 'post';
            form.action = `/admin/movies/${movieId}/delete`;
            
            // Add CSRF token if needed
            const csrfToken = document.querySelector('meta[name="_csrf"]');
            if (csrfToken) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = '_csrf';
                csrfInput.value = csrfToken.getAttribute('content');
                form.appendChild(csrfInput);
            }
            
            document.body.appendChild(form);
            form.submit();
        }
    };
}

function validateAndSubmitMovie(form) {
    const formData = new FormData(form);
    const movieData = Object.fromEntries(formData);
    
    // Basic validation
    if (!movieData.title || !movieData.genre) {
        showNotification('Please fill in all required fields', 'error');
        return;
    }
    
    // Submit form
    form.submit();
}

function populateEditForm(movie) {
    const form = document.getElementById('editMovieForm');
    form.action = `/admin/movies/${movie.id}/update`;
    
    // Populate form fields
    Object.keys(movie).forEach(key => {
        const input = form.querySelector(`[name="${key}"]`);
        if (input) {
            input.value = movie[key];
        }
    });
}

// Utility Functions
function showNotification(message, type = 'info') {
    const alertClass = type === 'error' ? 'alert-danger' : 
                      type === 'success' ? 'alert-success' : 
                      type === 'warning' ? 'alert-warning' : 'alert-info';
    
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;">
            <i class="fas fa-${type === 'error' ? 'exclamation-circle' : 
                               type === 'success' ? 'check-circle' : 
                               type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', alertHtml);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        const alert = document.querySelector('.alert:last-of-type');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 5000);
}

// AJAX Helper Functions
function makeAjaxRequest(url, method = 'GET', data = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    };
    
    if (data && method !== 'GET') {
        options.body = JSON.stringify(data);
    }
    
    return fetch(url, options)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        });
}

// Search and Filter Functions
function filterMovies(criteria) {
    const movies = document.querySelectorAll('.movie-card');
    movies.forEach(movie => {
        const title = movie.querySelector('.card-title').textContent.toLowerCase();
        const genre = movie.querySelector('.badge').textContent.toLowerCase();
        
        const matchesTitle = !criteria.search || title.includes(criteria.search.toLowerCase());
        const matchesGenre = !criteria.genre || genre === criteria.genre.toLowerCase();
        
        if (matchesTitle && matchesGenre) {
            movie.style.display = 'block';
        } else {
            movie.style.display = 'none';
        }
    });
}

// Export functions for global access
window.MovieBooking = {
    showNotification,
    makeAjaxRequest,
    filterMovies,
    editMovie: window.editMovie,
    deleteMovie: window.deleteMovie
};
