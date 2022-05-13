namespace BudgetCircleApi.BLL.Services
{
    using BudgetCircleApi.BLL.Interfaces;
    using BudgetCircleApi.BLL.Models;
    using BudgetCircleApi.BLL.Models.Request;
    using BudgetCircleApi.BLL.Models.Response;
    using BudgetCircleApi.DAL.Entities;
    using Microsoft.AspNetCore.Identity;
    using System;
    using System.Threading.Tasks;

    public class AuthorizationService : IAuthorizationService
    {
        private readonly UserManager<User> _userManager;
        private readonly IJWTGenerator _generator;
        private readonly IBudgetTypesService _budgetTypesService;

        private const string NotMatchingPasswords = "Passwords don't match";
        private const string NewPasswordIsSame = "New password should differ from old one";
        private const string WrongLoginOrPassword = "Wrong Login or Password";
        private const string WrongPassword = "Wrong Password";
        private const string SuccessfullSigningIn = "Successfully signed in";
        private const string UserAdded = "User was added";
        private const string UserExists = "Such user already exists";
        private const string PasswordWasChanged = "Password was changed";
        private const string TokenRefreshed = "Token successfully refreshed";
        private const string UserNotExist = "Such user doesn't exist";
        

        public AuthorizationService(
            UserManager<User> userManager,
            IJWTGenerator generator,
            IBudgetTypesService budgetTypesService)
        {
            _userManager = userManager;
            _generator = generator;
            _budgetTypesService = budgetTypesService;
        }

        public async Task<MessageResponse> ChangePassword(string userId, NewPasswordModel model)
        {
            if(model.NewPassword != model.ConfirmationPassword)
            {
                return new ErrorResponse(NotMatchingPasswords);
            }

            var user = await _userManager.FindByIdAsync(userId);
            if (!await _userManager.CheckPasswordAsync(user, model.Password))
            {
                return new ErrorResponse(WrongPassword);
            }

            if(model.Password == model.NewPassword)
            {
                return new ErrorResponse(NewPasswordIsSame);
            }

            string resetToken = await _userManager.GeneratePasswordResetTokenAsync(user);
            var result = await _userManager.ResetPasswordAsync(user, resetToken, model.NewPassword);

            if (result.Succeeded)
            {
                return new MessageResponse(PasswordWasChanged);
            }
            else
            {
                string errors = "";
                foreach (var error in result.Errors)
                {
                    errors += $"{error}\n";
                }

                return new ErrorResponse(errors);
            }
        }

        public async Task<MessageResponse> RefreshToken(string nameIdentifier)
        {
            var user = await _userManager.FindByIdAsync(nameIdentifier);
            if (user != null)
            {
                var token = await _generator.CreateToken(user);
                return new AuthorizationResponse(TokenRefreshed, token);
            }

            return new ErrorResponse(UserNotExist);
        }

        public async Task<MessageResponse> SignIn(SignInModel model)
        {
            var user = await _userManager.FindByNameAsync(model.Email);
            if (user != null && await _userManager.CheckPasswordAsync(user, model.Password))
            {
                var token = await _generator.CreateToken(user);
                return new AuthorizationResponse(SuccessfullSigningIn, token);
            }

            return new ErrorResponse(WrongLoginOrPassword);
        }

        public async Task<MessageResponse> SignUp(SignUpModel model)
        {
            var userExists = await _userManager.FindByNameAsync(model.Email);
            if (userExists != null)
            {
                return new ErrorResponse(UserExists);
            }

            if (string.Compare(model.Password, model.ConfirmationPassword) != 0)
            {
                return new ErrorResponse(NotMatchingPasswords);
            }

            User user = new User()
            {
                Email = model.Email,
                UserName = model.Email,
                SecurityStamp = Guid.NewGuid().ToString(),
            };

            var result = await _userManager.CreateAsync(user, model.Password);

            if (result.Succeeded)
            {
                await _userManager.AddToRoleAsync(user, UserRoles.User);
                await _budgetTypesService.AddDefaultBudgetType(user.Id);
                var token = await _generator.CreateToken(user);

                return new AuthorizationResponse(UserAdded, token);
            }
            else
            {
                string errors = "";
                foreach (var error in result.Errors)
                {
                    errors += $"{error.Description}\n";
                }

                return new ErrorResponse(errors);
            }
        }
    }
}
